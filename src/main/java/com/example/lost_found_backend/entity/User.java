package com.example.lost_found_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class User {
    private Long id;
    private String openid;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private Integer role;       // 角色：0=普通用户，1=管理员
    private Integer status;     // 状态：0=正常，1=禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
