package com.example.lost_found_backend.service;

import com.example.lost_found_backend.entity.Item;

import java.util.Map;

/**
 * 物品 Service
 */
public interface ItemService {

    /**
     * 发布物品（失物招领/寻物启事）
     */
    Item publish(Item item);

    /**
     * 根据ID获取物品详情
     */
    Item getById(Long id);

    /**
     * 编辑物品信息
     */
    Item update(Long id, Item item, String openid);

    /**
     * 撤销发布（将状态改为已失效）
     */
    void revoke(Long id, String openid);

    /**
     * 删除物品
     */
    void delete(Long id, String openid);

    /**
     * 更新物品状态
     */
    Item updateStatus(Long id, Integer status, String openid);

    /**
     * 分页查询物品列表
     *
     * @return { "list": [...], "total": N }
     */
    Map<String, Object> list(Integer type, Integer status, String category,
                             String keyword, String sort, String order,
                             int page, int size);

    /**
     * 查询我发布的物品
     */
    Map<String, Object> myItems(String openid, int page, int size);
}
