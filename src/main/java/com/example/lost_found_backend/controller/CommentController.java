package com.example.lost_found_backend.controller;

import com.example.lost_found_backend.common.Result;
import com.example.lost_found_backend.entity.Comment;
import com.example.lost_found_backend.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // POST /add - body: {itemId, content}
    @PostMapping("/add")
    public Result<Comment> add(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        Long itemId = Long.valueOf(body.get("itemId").toString());
        String content = (String) body.get("content");
        Comment comment = commentService.add(itemId, openid, content);
        return Result.ok("评论成功", comment);
    }

    // GET /list?itemId=&page=&size=
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam Long itemId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> result = commentService.listByItemId(itemId, page, size);
        return Result.ok(result);
    }

    // DELETE /{id}
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        commentService.delete(id, openid);
        return Result.okMsg("删除成功");
    }
}
