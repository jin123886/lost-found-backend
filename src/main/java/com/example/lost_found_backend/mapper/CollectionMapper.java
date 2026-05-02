package com.example.lost_found_backend.mapper;

import com.example.lost_found_backend.entity.Collection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CollectionMapper {
    int insert(Collection collection);
    int deleteByItemAndUser(@Param("itemId") Long itemId, @Param("userOpenid") String userOpenid);
    List<Collection> findByUser(@Param("userOpenid") String userOpenid, @Param("offset") int offset, @Param("size") int size);
    long countByUser(@Param("userOpenid") String userOpenid);
    int isCollected(@Param("itemId") Long itemId, @Param("userOpenid") String userOpenid);
}
