-- ============================================
-- 校园失物招领小程序 - 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS `lost_and_found`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `lost_and_found`;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `openid`          VARCHAR(64)  NOT NULL COMMENT '微信openid',
    `nickname`        VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `avatar_url`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `role`            INT          NOT NULL DEFAULT 0 COMMENT '角色：0=普通用户，1=管理员',
    `status`          INT          NOT NULL DEFAULT 0 COMMENT '状态：0=正常，1=禁用',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 物品表（失物招领 / 寻物启事）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `item` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `title`           VARCHAR(128) NOT NULL COMMENT '标题',
    `description`     TEXT         DEFAULT NULL COMMENT '详细描述',
    `category`        VARCHAR(32)  DEFAULT NULL COMMENT '分类',
    `type`            INT          NOT NULL DEFAULT 1 COMMENT '类型：1=失物招领，2=寻物启事',
    `status`          INT          NOT NULL DEFAULT 0 COMMENT '状态：0=待认领，1=已认领，2=已失效',
    `view_count`      INT          NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `comment_count`   INT          NOT NULL DEFAULT 0 COMMENT '评论次数',
    `collection_count` INT         NOT NULL DEFAULT 0 COMMENT '收藏次数',
    `location`        VARCHAR(128) DEFAULT NULL COMMENT '地点',
    `lost_time`       DATETIME     DEFAULT NULL COMMENT '丢失/捡到时间',
    `image_urls`      TEXT         DEFAULT NULL COMMENT '图片URL列表（JSON数组字符串）',
    `contact_info`    VARCHAR(128) DEFAULT NULL COMMENT '联系方式',
    `publisher_openid` VARCHAR(64) NOT NULL COMMENT '发布者微信openid',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY `idx_type_status` (`type`, `status`),
    KEY `idx_publisher` (`publisher_openid`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物品表';

-- ----------------------------
-- 认领表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `claim` (
    `id`               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `item_id`          BIGINT       NOT NULL COMMENT '物品ID',
    `claimant_openid`  VARCHAR(64)  NOT NULL COMMENT '认领者微信openid',
    `description`      TEXT         DEFAULT NULL COMMENT '认领描述',
    `status`           INT          NOT NULL DEFAULT 0 COMMENT '状态：0=待确认，1=已确认，2=已拒绝',
    `create_time`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY `idx_item_id` (`item_id`),
    KEY `idx_claimant` (`claimant_openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认领表';

-- ----------------------------
-- 评论表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `comment` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `item_id`         BIGINT       NOT NULL COMMENT '物品ID',
    `author_openid`   VARCHAR(64)  NOT NULL COMMENT '评论者openid',
    `content`         TEXT         NOT NULL COMMENT '评论内容',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ----------------------------
-- 收藏表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `collection` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `item_id`         BIGINT       NOT NULL COMMENT '物品ID',
    `user_openid`     VARCHAR(64)  NOT NULL COMMENT '用户openid',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_item_user` (`item_id`, `user_openid`),
    KEY `idx_user` (`user_openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';
