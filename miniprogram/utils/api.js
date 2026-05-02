const app = getApp();

function request(path, method, data) {
  return new Promise((resolve, reject) => {
    const header = { 'Content-Type': 'application/json' };
    const token = app.globalData.token || wx.getStorageSync('token');
    if (token) {
      header['Authorization'] = 'Bearer ' + token;
    }
    wx.request({
      url: app.globalData.baseUrl + path,
      method,
      data,
      header,
      success(res) {
        if (res.data.code === 200) {
          resolve(res.data.data);
        } else if (res.data.code === 401) {
          wx.removeStorageSync('token');
          app.globalData.token = '';
          wx.navigateTo({ url: '/pages/login/login' });
          reject(res.data.msg);
        } else {
          wx.showToast({ title: res.data.msg, icon: 'none' });
          reject(res.data.msg);
        }
      },
      fail(err) {
        wx.showToast({ title: '网络请求失败', icon: 'none' });
        reject(err);
      }
    });
  });
}

module.exports = {
  // 用户
  login: (code, nickname, avatarUrl) =>
    request('/api/user/login', 'POST', { code, nickname, avatarUrl }),
  getUserInfo: () => request('/api/user/info', 'GET'),

  // 物品
  publish: (data) => request('/api/item/publish', 'POST', data),
  getItemList: (params) => request('/api/item/list?' + queryString(params), 'GET'),
  getItemDetail: (id) => request('/api/item/' + id, 'GET'),
  getMyItems: (page = 1) => request('/api/item/my?page=' + page, 'GET'),
  updateItem: (id, data) => request('/api/item/' + id, 'PUT', data),
  updateItemStatus: (id, status) => request('/api/item/' + id + '/status', 'PUT', { status }),
  revokeItem: (id) => request('/api/item/' + id + '/revoke', 'PUT'),
  deleteItem: (id) => request('/api/item/' + id, 'DELETE'),

  // 认领
  applyClaim: (itemId, description) =>
    request('/api/claim/apply', 'POST', { itemId, description }),
  confirmClaim: (id) => request('/api/claim/' + id + '/confirm', 'PUT'),
  rejectClaim: (id) => request('/api/claim/' + id + '/reject', 'PUT'),
  getMyClaims: (page = 1) => request('/api/claim/my?page=' + page, 'GET'),
  getReceivedClaims: (page = 1) => request('/api/claim/received?page=' + page, 'GET'),

  // 上传
  uploadImage: (files) => {
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: app.globalData.baseUrl + '/api/upload/image',
        filePath: files,
        name: 'files',
        success(res) {
          const data = JSON.parse(res.data);
          if (data.code === 200) resolve(Array.isArray(data.data) ? data.data[0] : data.data);
          else reject(data.msg);
        },
        fail: reject
      });
    });
  },

  // 评论相关
  addComment(data) {
    return request('/api/comment/add', 'POST', data);
  },
  getComments(itemId, page = 1, size = 20) {
    return request(`/api/comment/list?itemId=${itemId}&page=${page}&size=${size}`, 'GET');
  },
  deleteComment(id) {
    return request(`/api/comment/${id}`, 'DELETE');
  },

  // 收藏相关
  toggleCollection(data) {
    return request('/api/collection/toggle', 'POST', data);
  },
  getMyCollections(page = 1, size = 10) {
    return request(`/api/collection/my?page=${page}&size=${size}`, 'GET');
  },
  checkCollection(itemId) {
    return request(`/api/collection/check?itemId=${itemId}`, 'GET');
  },
};

function queryString(obj) {
  const parts = [];
  for (let key in obj) {
    if (obj[key] !== null && obj[key] !== undefined && obj[key] !== '') {
      parts.push(key + '=' + encodeURIComponent(obj[key]));
    }
  }
  return parts.join('&');
}
