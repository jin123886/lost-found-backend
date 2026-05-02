package com.example.lost_found_backend.service.impl;

import com.example.lost_found_backend.common.BusinessException;
import com.example.lost_found_backend.entity.Item;
import com.example.lost_found_backend.mapper.ItemMapper;
import com.example.lost_found_backend.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物品 Service 实现
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public Item publish(Item item) {
        // 新发布的物品默认状态为"待认领"
        item.setStatus(0);
        int result = itemMapper.insert(item);
        if (result <= 0) {
            throw new RuntimeException("物品发布失败");
        }
        if (item.getId() == null) {
            throw new RuntimeException("物品ID未生成");
        }
        Item published = itemMapper.findById(item.getId());
        if (published == null) {
            throw new RuntimeException("物品查询失败");
        }
        return published;
    }

    @Override
    public Item getById(Long id) {
        Item item = itemMapper.findById(id);
        if (item != null) {
            itemMapper.incrementViewCount(id);
            item.setViewCount(item.getViewCount() != null ? item.getViewCount() + 1 : 1);
        }
        return item;
    }

    @Override
    @Transactional
    public Item update(Long id, Item updateData, String openid) {
        Item item = itemMapper.findById(id);
        if (item == null) {
            throw new IllegalArgumentException("物品不存在");
        }
        if (!item.getPublisherOpenid().equals(openid)) {
            throw new IllegalArgumentException("只能编辑自己发布的物品");
        }
        updateData.setId(id);
        itemMapper.updateById(updateData);
        return itemMapper.findById(id);
    }

    @Override
    @Transactional
    public Item updateStatus(Long id, Integer status, String openid) {
        if (status == null || (status != 0 && status != 1 && status != 2)) {
            throw new BusinessException("物品状态不正确");
        }
        Item item = itemMapper.findById(id);
        if (item == null) {
            throw new IllegalArgumentException("物品不存在");
        }
        if (!item.getPublisherOpenid().equals(openid)) {
            throw new IllegalArgumentException("只能更新自己发布的物品状态");
        }
        Item updateData = new Item();
        updateData.setId(id);
        updateData.setStatus(status);
        itemMapper.updateById(updateData);
        return itemMapper.findById(id);
    }

    @Override
    @Transactional
    public void revoke(Long id, String openid) {
        Item item = itemMapper.findById(id);
        if (item == null) {
            throw new IllegalArgumentException("物品不存在");
        }
        if (!item.getPublisherOpenid().equals(openid)) {
            throw new IllegalArgumentException("只能撤销自己发布的物品");
        }
        Item updateData = new Item();
        updateData.setId(id);
        updateData.setStatus(2); // 已失效
        itemMapper.updateById(updateData);
    }

    @Override
    @Transactional
    public void delete(Long id, String openid) {
        Item item = itemMapper.findById(id);
        if (item == null) {
            throw new IllegalArgumentException("物品不存在");
        }
        if (!item.getPublisherOpenid().equals(openid)) {
            throw new IllegalArgumentException("只能删除自己发布的物品");
        }
        itemMapper.deleteById(id);
    }

    @Override
    public Map<String, Object> list(Integer type, Integer status, String category,
                                     String keyword, String sort, String order,
                                     int page, int size) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;
        }
        int offset = (page - 1) * size;
        List<Item> items = itemMapper.list(type, status, category, keyword, sort, order, offset, size);
        long total = itemMapper.count(type, status, category, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("list", items);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @Override
    public Map<String, Object> myItems(String openid, int page, int size) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;
        }
        int offset = (page - 1) * size;
        List<Item> items = itemMapper.findByPublisher(openid, offset, size);
        long total = itemMapper.countByPublisher(openid);

        Map<String, Object> result = new HashMap<>();
        result.put("list", items);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }
}
