App({
  globalData: {
    token: '',
    userInfo: null,
    baseUrl: 'http://localhost:8080' // 部署后改为你的服务器域名
  },

  onLaunch() {
    const token = wx.getStorageSync('token');
    if (token) {
      this.globalData.token = token;
    }
  },

  checkLogin() {
    if (!this.globalData.token) {
      wx.navigateTo({ url: '/pages/login/login' });
      return false;
    }
    return true;
  }
});
