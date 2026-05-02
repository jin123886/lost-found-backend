package com.example.lost_found_backend.service;

import com.example.lost_found_backend.entity.Comment;
import java.util.Map;

public interface CommentService {

    Comment add(Long itemId, String authorOpenid, String content);

    Map<String, Object> listByItemId(Long itemId, int page, int size);

    void delete(Long id, String openid);
}
