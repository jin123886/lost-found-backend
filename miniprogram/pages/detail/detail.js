const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    itemId: null,
    item: null,
    images: [],
    baseUrl: app.globalData.baseUrl,
    currentUserOpenid: '',
    collected: false,
    comments: [],
    commentContent: '',
    commentsLoading: false,
    commentSubmitting: false,
    showClaimForm: false,
    claimDesc: '',
    submitting: false
  },

  onLoad(options) {
    if (!app.checkLogin()) return;
    const id = Number(options.id);
    this.setData({ itemId: id });
    this.loadCurrentUser();
    this.loadDetail();
    this.loadComments();
    this.loadCollectionStatus();
  },

  onShow() {
    if (!app.globalData.token || !this.data.itemId) return;
    this.loadCurrentUser();
    this.loadCollectionStatus();
  },

  loadCurrentUser() {
    api.getUserInfo().then(user => {
      app.globalData.userInfo = user;
      this.setData({ currentUserOpenid: user.openid || '' });
    }).catch(() => {});
  },

  loadDetail() {
    api.getItemDetail(this.data.itemId).then(item => {
      this.setData({
        item,
        images: this.parseImages(item.imageUrls)
      });
    });
  },

  loadComments() {
    this.setData({ commentsLoading: true });
    api.getComments(this.data.itemId).then(res => {
      this.setData({
        comments: res.list || [],
        commentsLoading: false
      });
    }).catch(() => this.setData({ commentsLoading: false }));
  },

  loadCollectionStatus() {
    api.checkCollection(this.data.itemId).then(res => {
      this.setData({ collected: !!res.collected });
    }).catch(() => {});
  },

  parseImages(imageUrls) {
    if (!imageUrls) return [];
    if (Array.isArray(imageUrls)) return imageUrls;
    try {
      const parsed = JSON.parse(imageUrls);
      return Array.isArray(parsed) ? parsed : [];
    } catch (e) {
      return [imageUrls];
    }
  },

  onClaimDescInput(e) {
    this.setData({ claimDesc: e.detail.value });
  },

  onCommentInput(e) {
    this.setData({ commentContent: e.detail.value });
  },

  toggleCollection() {
    api.toggleCollection({ itemId: this.data.itemId }).then(res => {
      this.setData({ collected: !!res.collected });
      this.loadDetail();
      wx.showToast({ title: res.collected ? '已收藏' : '已取消收藏', icon: 'none' });
    });
  },

  submitComment() {
    if (!this.data.commentContent.trim()) {
      return wx.showToast({ title: '请输入评论内容', icon: 'none' });
    }
    this.setData({ commentSubmitting: true });
    api.addComment({
      itemId: this.data.itemId,
      content: this.data.commentContent
    }).then(() => {
      this.setData({ commentContent: '', commentSubmitting: false });
      wx.showToast({ title: '评论成功', icon: 'success' });
      this.loadComments();
      this.loadDetail();
    }).catch(() => this.setData({ commentSubmitting: false }));
  },

  deleteComment(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '删除评论',
      content: '确认删除这条评论吗？',
      success: (res) => {
        if (!res.confirm) return;
        api.deleteComment(id).then(() => {
          wx.showToast({ title: '删除成功', icon: 'success' });
          this.loadComments();
          this.loadDetail();
        });
      }
    });
  },

  showClaim() {
    this.setData({ showClaimForm: true });
  },

  hideClaim() {
    this.setData({ showClaimForm: false, claimDesc: '' });
  },

  submitClaim() {
    if (!this.data.claimDesc.trim()) {
      return wx.showToast({ title: '请填写认领说明', icon: 'none' });
    }
    this.setData({ submitting: true });
    api.applyClaim(this.data.item.id, this.data.claimDesc).then(() => {
      wx.showToast({ title: '认领申请已提交', icon: 'success' });
      this.setData({ submitting: false, showClaimForm: false, claimDesc: '' });
    }).catch(() => this.setData({ submitting: false }));
  }
});
