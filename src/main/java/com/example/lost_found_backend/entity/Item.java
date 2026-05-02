package com.example.lost_found_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 物品实体
 */
@Data
public class Item {
    private Long id;
    private String title;
    private String description;
    private String category;
    /** 类型：1=失物招领（捡到东西），2=寻物启事（丢了东西） */
    private Integer type;
    /** 状态：0=待认领，1=已认领，2=已失效 */
    private Integer status;
    private Integer viewCount;        // 浏览次数
    private Integer commentCount;     // 评论次数
    private Integer collectionCount;  // 收藏次数
    private String location;
    private LocalDateTime lostTime;
    /** 图片URL列表（JSON数组字符串） */
    private String imageUrls;
    private String contactInfo;
    /** 发布者微信openid */
    private String publisherOpenid;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
