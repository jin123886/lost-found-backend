package com.example.lost_found_backend.service.impl;

import com.example.lost_found_backend.common.BusinessException;
import com.example.lost_found_backend.entity.Collection;
import com.example.lost_found_backend.entity.Item;
import com.example.lost_found_backend.mapper.CollectionMapper;
import com.example.lost_found_backend.mapper.ItemMapper;
import com.example.lost_found_backend.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionMapper collectionMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public boolean toggle(Long itemId, String userOpenid) {
        if (itemId == null || itemId <= 0) {
            throw new BusinessException("物品ID不能为空");
        }
        if (userOpenid == null || userOpenid.isEmpty()) {
            throw new BusinessException("用户openid不能为空");
        }

        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }

        int collected = collectionMapper.isCollected(itemId, userOpenid);
        if (collected > 0) {
            // 已收藏，取消收藏
            collectionMapper.deleteByItemAndUser(itemId, userOpenid);
            itemMapper.decrementCollectionCount(itemId);
            return false;
        } else {
            // 未收藏，添加收藏
            Collection collection = new Collection();
            collection.setItemId(itemId);
            collection.setUserOpenid(userOpenid);
            collectionMapper.insert(collection);
            itemMapper.incrementCollectionCount(itemId);
            return true;
        }
    }

    @Override
    public Map<String, Object> myCollections(String userOpenid, int page, int size) {
        if (userOpenid == null || userOpenid.isEmpty()) {
            throw new BusinessException("用户openid不能为空");
        }
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;
        }

        int offset = (page - 1) * size;
        List<Collection> list = collectionMapper.findByUser(userOpenid, offset, size);
        long total = collectionMapper.countByUser(userOpenid);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @Override
    public boolean isCollected(Long itemId, String userOpenid) {
        if (itemId == null || itemId <= 0) {
            return false;
        }
        if (userOpenid == null || userOpenid.isEmpty()) {
            return false;
        }
        return collectionMapper.isCollected(itemId, userOpenid) > 0;
    }
}
