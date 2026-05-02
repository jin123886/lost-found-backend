package com.example.lost_found_backend.controller;

import com.example.lost_found_backend.common.Result;
import com.example.lost_found_backend.entity.Claim;
import com.example.lost_found_backend.service.ClaimService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认领控制器 — 认领申请 / 确认 / 查询
 */
@RestController
@RequestMapping("/api/claim")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    /**
     * 提交认领申请
     */
    @PostMapping("/apply")
    public Result<Claim> apply(@RequestBody Map<String, Object> body,
                                HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        Long itemId = body.get("itemId") != null
                ? Long.valueOf(body.get("itemId").toString()) : null;
        String description = (String) body.getOrDefault("description", "");

        if (itemId == null) {
            return Result.fail("物品ID不能为空");
        }

        Claim claim = claimService.apply(itemId, openid, description);
        return Result.ok("认领申请已提交", claim);
    }

    /**
     * 确认认领（发布者操作）
     */
    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id, HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        claimService.confirm(id, openid);
        return Result.okMsg("已确认认领，物品标记为已认领");
    }

    /**
     * 拒绝认领（发布者操作）
     */
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        claimService.reject(id, openid);
        return Result.okMsg("已拒绝该认领申请");
    }

    /**
     * 我发起的认领申请列表
     */
    @GetMapping("/my")
    public Result<Map<String, Object>> myClaims(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        Map<String, Object> result = claimService.myClaims(openid, page, size);
        return Result.ok(result);
    }

    /**
     * 我收到的认领申请列表
     */
    @GetMapping("/received")
    public Result<Map<String, Object>> receivedClaims(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        Map<String, Object> result = claimService.receivedClaims(openid, page, size);
        return Result.ok(result);
    }
}
