package com.example.lost_found_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long itemId;
    private String authorOpenid;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联字段（查询时填充）
    private String authorNickname;
    private String authorAvatarUrl;
}
