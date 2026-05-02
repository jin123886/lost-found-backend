package com.example.lost_found_backend.service.impl;

import com.example.lost_found_backend.common.BusinessException;
import com.example.lost_found_backend.entity.Comment;
import com.example.lost_found_backend.entity.Item;
import com.example.lost_found_backend.mapper.CommentMapper;
import com.example.lost_found_backend.mapper.ItemMapper;
import com.example.lost_found_backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public Comment add(Long itemId, String authorOpenid, String content) {
        if (itemId == null || itemId <= 0) {
            throw new BusinessException("物品ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("评论内容不能为空");
        }
        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }

        Comment comment = new Comment();
        comment.setItemId(itemId);
        comment.setAuthorOpenid(authorOpenid);
        comment.setContent(content.trim());

        commentMapper.insert(comment);

        // 重新查询以获取完整数据（含作者信息）
        Comment result = commentMapper.findById(comment.getId());

        // 增加物品评论数
        itemMapper.incrementCommentCount(itemId);

        return result;
    }

    @Override
    public Map<String, Object> listByItemId(Long itemId, int page, int size) {
        if (itemId == null || itemId <= 0) {
            throw new BusinessException("物品ID不能为空");
        }
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 20;
        }
        int offset = (page - 1) * size;

        List<Comment> list = commentMapper.findByItemId(itemId, offset, size);
        long total = commentMapper.countByItemId(itemId);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @Override
    @Transactional
    public void delete(Long id, String openid) {
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        if (!comment.getAuthorOpenid().equals(openid)) {
            throw new BusinessException("无权删除该评论");
        }

        commentMapper.deleteById(id);

        // 减少物品评论数
        itemMapper.decrementCommentCount(comment.getItemId());
    }
}
