package com.example.lost_found_backend.controller;

import com.example.lost_found_backend.common.JwtUtils;
import com.example.lost_found_backend.common.Result;
import com.example.lost_found_backend.entity.User;
import com.example.lost_found_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器 — 微信小程序登录
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    /**
     * 微信小程序登录（真实模式）
     * 请求体: { "code": "wx.login()返回的code", "nickname": "xxx", "avatarUrl": "xxx" }
     * 返回: { "token": "jwt...", "user": {...} }
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        String nickname = body.getOrDefault("nickname", "微信用户");
        String avatarUrl = body.getOrDefault("avatarUrl", "");

        if (code == null || code.isEmpty()) {
            return Result.fail("登录凭证code不能为空");
        }

        // 调用微信接口换取 openid，再登录/注册
        User user = userService.loginByWechatCode(code, nickname, avatarUrl);

        // 生成 JWT Token
        String token = jwtUtils.generateToken(user.getOpenid());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.ok("登录成功", data);
    }

    /**
     * 获取当前用户信息（需 Token）
     */
    @GetMapping("/info")
    public Result<User> info(HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        User user = userService.getByOpenid(openid);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        return Result.ok(user);
    }
}
