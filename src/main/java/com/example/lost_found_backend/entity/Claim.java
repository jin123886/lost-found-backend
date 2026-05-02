package com.example.lost_found_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 认领实体
 */
@Data
public class Claim {
    private Long id;
    private Long itemId;
    private String claimantOpenid;
    private String description;
    /** 状态：0=待确认，1=已确认，2=已拒绝 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // ---- 关联字段（查询时联表填充） ----
    /** 物品标题 */
    private String itemTitle;
    /** 物品类型 */
    private Integer itemType;
    /** 发布者openid */
    private String publisherOpenid;
}
