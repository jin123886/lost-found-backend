package com.example.lost_found_backend.controller;

import com.example.lost_found_backend.common.Result;
import com.example.lost_found_backend.service.CollectionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/collection")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    // POST /toggle - body: {itemId}
    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggle(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        Long itemId = Long.valueOf(body.get("itemId").toString());
        boolean collected = collectionService.toggle(itemId, openid);
        Map<String, Object> data = new HashMap<>();
        data.put("collected", collected);
        return Result.ok(collected ? "已收藏" : "已取消收藏", data);
    }

    // GET /my?page=&size=
    @GetMapping("/my")
    public Result<Map<String, Object>> myCollections(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        Map<String, Object> result = collectionService.myCollections(openid, page, size);
        return Result.ok(result);
    }

    // GET /check?itemId=
    @GetMapping("/check")
    public Result<Map<String, Object>> check(@RequestParam Long itemId, HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        boolean collected = collectionService.isCollected(itemId, openid);
        Map<String, Object> data = new HashMap<>();
        data.put("collected", collected);
        return Result.ok(data);
    }
}
