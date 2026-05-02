package com.example.lost_found_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Collection {
    private Long id;
    private Long itemId;
    private String userOpenid;
    private LocalDateTime createTime;

    // 关联字段（查询时填充）
    private String itemTitle;
    private Integer itemType;
    private Integer itemStatus;
    private String itemImageUrls;
}
