package com.example.lost_found_backend.service;

import java.util.Map;

public interface CollectionService {

    /**
     * 切换收藏状态
     * @param itemId 物品ID
     * @param userOpenid 用户openid
     * @return true=已收藏, false=已取消
     */
    boolean toggle(Long itemId, String userOpenid);

    /**
     * 查询我的收藏列表（分页）
     * @param userOpenid 用户openid
     * @param page 页码
     * @param size 每页条数
     * @return {list, total, page, size}
     */
    Map<String, Object> myCollections(String userOpenid, int page, int size);

    /**
     * 判断是否已收藏
     * @param itemId 物品ID
     * @param userOpenid 用户openid
     * @return true=已收藏
     */
    boolean isCollected(Long itemId, String userOpenid);
}
