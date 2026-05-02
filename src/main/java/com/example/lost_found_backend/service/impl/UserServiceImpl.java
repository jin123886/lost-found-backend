package com.example.lost_found_backend.service.impl;

import com.example.lost_found_backend.config.WechatConfig;
import com.example.lost_found_backend.entity.User;
import com.example.lost_found_backend.mapper.UserMapper;
import com.example.lost_found_backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * 用户 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final WechatConfig wechatConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public User loginByWechatCode(String code, String nickname, String avatarUrl) {
        String openid;

        // 如果配置了真实的 AppID，调用微信接口换取 openid
        if (wechatConfig.getAppid() != null && !wechatConfig.getAppid().isEmpty()
                && !"your-appid-here".equals(wechatConfig.getAppid())) {
            openid = code2Session(code);
        } else {
            // 开发阶段：未配置真实 AppID 时，直接用 code 模拟 openid
            log.warn("未配置微信 AppID/Secret，使用 code 作为 openid 模拟登录");
            openid = code;
        }

        return loginByOpenid(openid, nickname, avatarUrl);
    }

    /**
     * 调用微信 code2Session 接口换取 openid
     */
    private String code2Session(String code) {
        try {
            String url = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    wechatConfig.getAppid(), wechatConfig.getSecret(), code);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);

            if (result.get("errcode") != null && (int) result.get("errcode") != 0) {
                throw new RuntimeException("微信登录失败: " + result.get("errmsg"));
            }

            return (String) result.get("openid");
        } catch (Exception e) {
            log.error("微信 code2Session 调用失败", e);
            throw new RuntimeException("微信登录失败，请稍后重试");
        }
    }

    @Override
    public User loginByOpenid(String openid, String nickname, String avatarUrl) {
        User user = userMapper.findByOpenid(openid);
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname(nickname);
            user.setAvatarUrl(avatarUrl);
            int result = userMapper.insert(user);
            if (result <= 0) {
                throw new RuntimeException("用户创建失败");
            }
            // 重新查询以获取完整数据（包括id、createTime、updateTime）
            user = userMapper.findByOpenid(openid);
        } else {
            user.setNickname(nickname);
            user.setAvatarUrl(avatarUrl);
            int result = userMapper.updateById(user);
            if (result <= 0) {
                throw new RuntimeException("用户信息更新失败");
            }
        }
        return user;
    }

    @Override
    public User getByOpenid(String openid) {
        return userMapper.findByOpenid(openid);
    }
}
