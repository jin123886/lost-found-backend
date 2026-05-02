package com.example.lost_found_backend.service.impl;

import com.example.lost_found_backend.entity.Claim;
import com.example.lost_found_backend.entity.Item;
import com.example.lost_found_backend.mapper.ClaimMapper;
import com.example.lost_found_backend.mapper.ItemMapper;
import com.example.lost_found_backend.service.ClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认领 Service 实现
 */
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimMapper claimMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public Claim apply(Long itemId, String claimantOpenid, String description) {
        // 检查物品是否存在
        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("物品不存在");
        }
        // 只有"待认领"状态的物品才能申请认领
        if (item.getStatus() != 0) {
            throw new IllegalArgumentException("该物品当前不可认领");
        }
        // 不能认领自己发布的物品
        if (item.getPublisherOpenid().equals(claimantOpenid)) {
            throw new IllegalArgumentException("不能认领自己发布的物品");
        }
        // 检查是否已经申请过
        int count = claimMapper.countByItemAndClaimant(itemId, claimantOpenid);
        if (count > 0) {
            throw new IllegalArgumentException("您已对该物品提交过认领申请，请勿重复提交");
        }

        Claim claim = new Claim();
        claim.setItemId(itemId);
        claim.setClaimantOpenid(claimantOpenid);
        claim.setDescription(description);
        claim.setStatus(0); // 待确认
        int result = claimMapper.insert(claim);
        if (result <= 0) {
            throw new RuntimeException("认领申请提交失败");
        }
        if (claim.getId() == null) {
            throw new RuntimeException("认领申请ID未生成");
        }
        Claim applied = claimMapper.findById(claim.getId());
        if (applied == null) {
            throw new RuntimeException("认领申请查询失败");
        }
        return applied;
    }

    @Override
    @Transactional
    public void confirm(Long claimId, String publisherOpenid) {
        Claim claim = claimMapper.findById(claimId);
        if (claim == null) {
            throw new IllegalArgumentException("认领申请不存在");
        }
        // 只有物品发布者才能确认
        if (!claim.getPublisherOpenid().equals(publisherOpenid)) {
            throw new IllegalArgumentException("只有物品发布者才能确认认领");
        }
        if (claim.getStatus() != 0) {
            throw new IllegalArgumentException("该认领申请已被处理");
        }

        // 更新认领状态
        claimMapper.updateStatus(claimId, 1); // 已确认

        // 更新物品状态为"已认领"
        Item updateData = new Item();
        updateData.setId(claim.getItemId());
        updateData.setStatus(1);
        itemMapper.updateById(updateData);

        // 拒绝同一物品的其他待处理申请
        claimMapper.rejectPendingByItemId(claim.getItemId(), claimId);
    }

    @Override
    @Transactional
    public void reject(Long claimId, String publisherOpenid) {
        Claim claim = claimMapper.findById(claimId);
        if (claim == null) {
            throw new IllegalArgumentException("认领申请不存在");
        }
        if (!claim.getPublisherOpenid().equals(publisherOpenid)) {
            throw new IllegalArgumentException("只有物品发布者才能拒绝认领");
        }
        if (claim.getStatus() != 0) {
            throw new IllegalArgumentException("该认领申请已被处理");
        }

        claimMapper.updateStatus(claimId, 2); // 已拒绝
    }

    @Override
    public Map<String, Object> myClaims(String claimantOpenid, int page, int size) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;
        }
        int offset = (page - 1) * size;
        List<Claim> claims = claimMapper.findByClaimant(claimantOpenid, offset, size);
        long total = claimMapper.countByClaimant(claimantOpenid);

        Map<String, Object> result = new HashMap<>();
        result.put("list", claims);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @Override
    public Map<String, Object> receivedClaims(String publisherOpenid, int page, int size) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;
        }
        int offset = (page - 1) * size;
        List<Claim> claims = claimMapper.findByPublisher(publisherOpenid, offset, size);
        long total = claimMapper.countByPublisher(publisherOpenid);

        Map<String, Object> result = new HashMap<>();
        result.put("list", claims);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }
}
