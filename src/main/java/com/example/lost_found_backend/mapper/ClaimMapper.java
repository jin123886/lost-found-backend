package com.example.lost_found_backend.mapper;

import com.example.lost_found_backend.entity.Claim;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 认领 Mapper
 */
@Mapper
public interface ClaimMapper {

    int insert(Claim claim);

    Claim findById(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 查询某人发起的认领申请列表 */
    List<Claim> findByClaimant(@Param("claimantOpenid") String claimantOpenid,
                               @Param("offset") int offset,
                               @Param("size") int size);

    /** 查询某人收到的认领申请列表（发布者的物品被申请认领） */
    List<Claim> findByPublisher(@Param("publisherOpenid") String publisherOpenid,
                                @Param("offset") int offset,
                                @Param("size") int size);

    long countByClaimant(@Param("claimantOpenid") String claimantOpenid);

    long countByPublisher(@Param("publisherOpenid") String publisherOpenid);

    int rejectPendingByItemId(@Param("itemId") Long itemId, @Param("excludeId") Long excludeId);

    /** 查询某物品的认领申请数（防止重复申请） */
    int countByItemAndClaimant(@Param("itemId") Long itemId,
                               @Param("claimantOpenid") String claimantOpenid);
}
