package com.example.lost_found_backend.mapper;

import com.example.lost_found_backend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {
    int insert(Comment comment);

    Comment findById(@Param("id") Long id);

    List<Comment> findByItemId(@Param("itemId") Long itemId, @Param("offset") int offset, @Param("size") int size);

    long countByItemId(@Param("itemId") Long itemId);

    int deleteById(@Param("id") Long id);
}
