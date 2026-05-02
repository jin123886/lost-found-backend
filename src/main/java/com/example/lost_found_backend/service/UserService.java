package com.example.lost_found_backend.service;

import com.example.lost_found_backend.entity.User;

/**
 * 用户 Service
 */
public interface UserService {

    /**
     * 微信 code2Session 登录：用 code 换取 openid，查找或创建用户
     */
    User loginByWechatCode(String code, String nickname, String avatarUrl);

    /**
     * 根据 openid 查找或创建用户
     */
    User loginByOpenid(String openid, String nickname, String avatarUrl);

    /**
     * 根据 openid 获取用户
     */
    User getByOpenid(String openid);
}
