package com.example.lost_found_backend.controller;

import com.example.lost_found_backend.common.Result;
import com.example.lost_found_backend.entity.Item;
import com.example.lost_found_backend.service.ItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 物品控制器 — 发布 / 浏览 / 管理
 */
@RestController
@RequestMapping("/api/item")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 发布物品（失物招领 / 寻物启事）
     */
    @PostMapping("/publish")
    public Result<Item> publish(@RequestBody Map<String, Object> body,
                                 HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");

        Item item = new Item();
        item.setTitle((String) body.get("title"));
        item.setDescription((String) body.get("description"));
        item.setCategory((String) body.get("category"));
        item.setType((Integer) body.get("type"));
        item.setLocation((String) body.get("location"));

        Object lostTimeObj = body.get("lostTime");
        if (lostTimeObj != null && !lostTimeObj.toString().isEmpty()) {
            item.setLostTime(LocalDateTime.parse(lostTimeObj.toString().replace(" ", "T")));
        }

        item.setImageUrls((String) body.get("imageUrls"));
        item.setContactInfo((String) body.get("contactInfo"));
        item.setPublisherOpenid(openid);

        if (item.getTitle() == null || item.getTitle().isEmpty()) {
            return Result.fail("物品名称不能为空");
        }
        if (item.getType() == null || (item.getType() != 1 && item.getType() != 2)) {
            return Result.fail("物品类型不正确（1=失物招领, 2=寻物启事）");
        }

        Item saved = itemService.publish(item);
        return Result.ok("发布成功", saved);
    }

    /**
     * 获取物品详情
     */
    @GetMapping("/{id}")
    public Result<Item> detail(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.fail("参数错误，id 必须大于 0");
        }
        Item item = itemService.getById(id);
        if (item == null) {
            return Result.fail("物品不存在");
        }
        return Result.ok(item);
    }

    /**
     * 物品列表（支持筛选/搜索/排序/分页）
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "create_time") String sort,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (!"create_time".equals(sort)) {
            sort = "create_time";
        }
        if (!"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)) {
            order = "desc";
        }

        Map<String, Object> result = itemService.list(type, status, category, keyword, sort, order, page, size);
        return Result.ok(result);
    }

    /**
     * 我发布的物品列表
     */
    @GetMapping("/my")
    public Result<Map<String, Object>> myItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        Map<String, Object> result = itemService.myItems(openid, page, size);
        return Result.ok(result);
    }

    /**
     * 编辑物品信息
     */
    @PutMapping("/{id}")
    public Result<Item> update(@PathVariable Long id,
                                @RequestBody Map<String, Object> body,
                                HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");

        Item updateData = new Item();
        if (body.containsKey("title")) updateData.setTitle((String) body.get("title"));
        if (body.containsKey("description")) updateData.setDescription((String) body.get("description"));
        if (body.containsKey("category")) updateData.setCategory((String) body.get("category"));
        if (body.containsKey("location")) updateData.setLocation((String) body.get("location"));

        Object lostTimeObj = body.get("lostTime");
        if (lostTimeObj != null && !lostTimeObj.toString().isEmpty()) {
            updateData.setLostTime(LocalDateTime.parse(lostTimeObj.toString().replace(" ", "T")));
        }

        if (body.containsKey("imageUrls")) updateData.setImageUrls((String) body.get("imageUrls"));
        if (body.containsKey("contactInfo")) updateData.setContactInfo((String) body.get("contactInfo"));

        Item updated = itemService.update(id, updateData, openid);
        return Result.ok("编辑成功", updated);
    }

    /**
     * 更新物品状态
     */
    @PutMapping("/{id}/status")
    public Result<Item> updateStatus(@PathVariable Long id,
                                     @RequestBody Map<String, Object> body,
                                     HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        Object statusObj = body.get("status");
        if (statusObj == null) {
            return Result.fail("物品状态不能为空");
        }

        Integer status = Integer.valueOf(statusObj.toString());
        Item updated = itemService.updateStatus(id, status, openid);
        return Result.ok("状态更新成功", updated);
    }

    /**
     * 撤销发布
     */
    @PutMapping("/{id}/revoke")
    public Result<Void> revoke(@PathVariable Long id, HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        itemService.revoke(id, openid);
        return Result.okMsg("已撤销发布");
    }

    /**
     * 删除物品
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        itemService.delete(id, openid);
        return Result.okMsg("删除成功");
    }
}
