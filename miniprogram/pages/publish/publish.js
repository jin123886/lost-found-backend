const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    type: 1, // 1=失物招领, 2=寻物启事
    title: '',
    description: '',
    category: '',
    location: '',
    lostTime: '',
    contactInfo: '',
    images: [],
    submitting: false,
    baseUrl: app.globalData.baseUrl,
    categories: ['电子产品', '证件', '生活用品', '服饰', '书籍', '其他']
  },

  onLoad() {
    if (!app.checkLogin()) return;
  },

  onTypeChange(e) {
    const type = e.currentTarget.dataset.type;
    if (type !== undefined) {
      this.setData({ type: Number(type) });
      return;
    }
    this.setData({ type: parseInt(e.detail.value, 10) });
  },

  onTitleInput(e) {
    this.setData({ title: e.detail.value });
  },

  onDescriptionInput(e) {
    this.setData({ description: e.detail.value });
  },

  onLocationInput(e) {
    this.setData({ location: e.detail.value });
  },

  onContactInput(e) {
    this.setData({ contactInfo: e.detail.value });
  },

  onCategoryChange(e) {
    this.setData({ category: this.data.categories[e.detail.value] });
  },

  onDateChange(e) {
    this.setData({ lostTime: e.detail.value });
  },

  chooseImage() {
    wx.chooseMedia({
      count: 3 - this.data.images.length,
      mediaType: ['image'],
      success: (res) => {
        const files = res.tempFiles.map(f => f.tempFilePath);
        wx.showLoading({ title: '上传中...' });

        // 逐个上传
        const uploads = files.map(f => api.uploadImage(f));
        Promise.all(uploads).then(urls => {
          wx.hideLoading();
          this.setData({ images: this.data.images.concat(urls) });
        }).catch(() => wx.hideLoading());
      }
    });
  },

  removeImage(e) {
    const idx = e.currentTarget.dataset.idx;
    const images = this.data.images.filter((_, i) => i !== idx);
    this.setData({ images });
  },

  submit() {
    if (!this.data.title.trim()) {
      return wx.showToast({ title: '请输入物品名称', icon: 'none' });
    }

    this.setData({ submitting: true });

    api.publish({
      title: this.data.title,
      description: this.data.description,
      category: this.data.category,
      type: this.data.type,
      location: this.data.location,
      lostTime: this.data.lostTime,
      imageUrls: JSON.stringify(this.data.images),
      contactInfo: this.data.contactInfo
    }).then(() => {
      wx.showToast({ title: '发布成功', icon: 'success' });
      setTimeout(() => wx.switchTab({ url: '/pages/index/index' }), 1000);
    }).catch(() => this.setData({ submitting: false }));
  }
});
