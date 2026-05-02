package com.example.lost_found_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatConfig {
    /** 小程序 AppID — 需替换为你自己的 */
    private String appid = "";
    /** 小程序 AppSecret — 需替换为你自己的 */
    private String secret = "";
}
