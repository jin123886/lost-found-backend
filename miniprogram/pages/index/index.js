const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    type: null,      // null=全部, 1=失物招领, 2=寻物启事
    status: 0,       // 0=待认领
    category: '',
    keyword: '',
    page: 1,
    list: [],
    total: 0,
    loading: false,
    noMore: false,
    // 类型筛选
    typeTabs: [
      { label: '全部', value: null },
      { label: '失物招领', value: 1 },
      { label: '寻物启事', value: 2 }
    ]
  },

  onLoad() {
    if (!app.checkLogin()) return;
    this.loadList();
  },

  onShow() {
    if (app.globalData.token) {
      this.loadList();
    }
  },

  loadList() {
    if (this.data.loading || this.data.noMore) return;
    this.setData({ loading: true });

    api.getItemList({
      type: this.data.type,
      status: this.data.status,
      category: this.data.category,
      keyword: this.data.keyword,
      page: this.data.page
    }).then(res => {
      const list = this.data.page === 1 ? res.list : this.data.list.concat(res.list);
      this.setData({
        list,
        total: res.total,
        loading: false,
        noMore: list.length >= res.total
      });
    }).catch(() => this.setData({ loading: false }));
  },

  onSwitchType(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({ type, page: 1, list: [], noMore: false }, () => this.loadList());
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  onSearch() {
    this.setData({ page: 1, list: [], noMore: false }, () => this.loadList());
  },

  onLoadMore() {
    if (this.data.noMore) return;
    this.setData({ page: this.data.page + 1 }, () => this.loadList());
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/detail/detail?id=' + id });
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.setData({ page: 1, list: [], noMore: false }, () => {
      this.loadList();
      wx.stopPullDownRefresh();
    });
  }
});
