const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    userInfo: null,
    myItems: [],
    myClaims: [],
    receivedClaims: [],
    myCollections: [],
    activeTab: 'items' // items | claims | received | collections
  },

  onLoad() {
    if (!app.checkLogin()) return;
  },

  onShow() {
    if (!app.globalData.token) return;
    this.loadUserInfo();
    this.loadData();
  },

  loadUserInfo() {
    api.getUserInfo().then(user => {
      app.globalData.userInfo = user;
      this.setData({ userInfo: user });
    });
  },

  loadData() {
    api.getMyItems().then(res => this.setData({ myItems: res.list }));
    api.getMyClaims().then(res => this.setData({ myClaims: res.list }));
    api.getReceivedClaims().then(res => this.setData({ receivedClaims: res.list }));
    api.getMyCollections().then(res => this.setData({ myCollections: res.list }));
  },

  switchTab(e) {
    this.setData({ activeTab: e.currentTarget.dataset.tab });
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/detail/detail?id=' + id });
  },

  revokeItem(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认撤销',
      content: '撤销后该物品将标记为已失效',
      success: (res) => {
        if (res.confirm) {
          api.revokeItem(id).then(() => {
            wx.showToast({ title: '已撤销', icon: 'success' });
            this.loadData();
          });
        }
      }
    });
  },

  deleteItem(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认删除',
      content: '删除后无法恢复',
      success: (res) => {
        if (res.confirm) {
          api.deleteItem(id).then(() => {
            wx.showToast({ title: '已删除', icon: 'success' });
            this.loadData();
          });
        }
      }
    });
  },

  confirmClaim(e) {
    const id = e.currentTarget.dataset.id;
    api.confirmClaim(id).then(() => {
      wx.showToast({ title: '已确认认领', icon: 'success' });
      this.loadData();
    });
  },

  rejectClaim(e) {
    const id = e.currentTarget.dataset.id;
    api.rejectClaim(id).then(() => {
      wx.showToast({ title: '已拒绝', icon: 'success' });
      this.loadData();
    });
  }
});
