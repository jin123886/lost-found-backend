const api = require('../../utils/api');
const app = getApp();

Page({
  data: { canLogin: false },

  onLoad() {
    // 如果已登录，直接回首页
    if (app.globalData.token) {
      wx.switchTab({ url: '/pages/index/index' });
    }
  },

  handleLogin() {
    wx.showLoading({ title: '登录中...' });
    wx.login({
      success: (res) => {
        if (res.code) {
          // 获取用户信息
          wx.getUserProfile({
            desc: '用于完善个人资料',
            success: (profile) => {
              const userInfo = profile.userInfo;
              api.login(res.code, userInfo.nickName, userInfo.avatarUrl).then(data => {
                wx.hideLoading();
                app.globalData.token = data.token;
                app.globalData.userInfo = data.user;
                wx.setStorageSync('token', data.token);
                wx.showToast({ title: '登录成功', icon: 'success' });
                setTimeout(() => wx.switchTab({ url: '/pages/index/index' }), 1000);
              }).catch(() => wx.hideLoading());
            },
            fail: () => {
              // 用户拒绝授权，使用默认信息登录
              api.login(res.code, '微信用户', '').then(data => {
                wx.hideLoading();
                app.globalData.token = data.token;
                app.globalData.userInfo = data.user;
                wx.setStorageSync('token', data.token);
                wx.showToast({ title: '登录成功', icon: 'success' });
                setTimeout(() => wx.switchTab({ url: '/pages/index/index' }), 1000);
              }).catch(() => wx.hideLoading());
            }
          });
        }
      },
      fail: () => {
        wx.hideLoading();
        wx.showToast({ title: '登录失败', icon: 'none' });
      }
    });
  }
});
