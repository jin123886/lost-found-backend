package com.example.lost_found_backend.service;

import com.example.lost_found_backend.entity.Claim;

import java.util.Map;

/**
 * 认领 Service
 */
public interface ClaimService {

    /**
     * 提交认领申请
     */
    Claim apply(Long itemId, String claimantOpenid, String description);

    /**
     * 确认认领（发布者操作）
     */
    void confirm(Long claimId, String publisherOpenid);

    /**
     * 拒绝认领（发布者操作）
     */
    void reject(Long claimId, String publisherOpenid);

    /**
     * 查询我发起的认领申请
     */
    Map<String, Object> myClaims(String claimantOpenid, int page, int size);

    /**
     * 查询我收到的认领申请（我的物品被申请认领）
     */
    Map<String, Object> receivedClaims(String publisherOpenid, int page, int size);
}
