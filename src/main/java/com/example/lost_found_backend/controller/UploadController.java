package com.example.lost_found_backend.controller;

import com.example.lost_found_backend.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传控制器 — 小程序图片上传
 */
@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @Value("${upload.path:uploads}")
    private String uploadPath;

    /**
     * 上传图片（支持单张或多张）
     */
    @PostMapping("/image")
    public Result<List<String>> uploadImage(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return Result.fail("请选择要上传的图片");
        }

        List<String> urls = new ArrayList<>();
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        try {
            Path uploadDir = Paths.get(uploadPath, dateDir);
            Files.createDirectories(uploadDir);

            for (MultipartFile file : files) {
                // 生成唯一文件名
                String originalName = file.getOriginalFilename();
                String suffix = "";
                if (originalName != null && originalName.contains(".")) {
                    suffix = originalName.substring(originalName.lastIndexOf("."));
                }
                String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;

                // 保存文件
                Path targetPath = uploadDir.resolve(newFileName);
                file.transferTo(targetPath.toFile());

                // 返回访问URL（生产环境应替换为CDN地址）
                String url = "/uploads/" + dateDir + "/" + newFileName;
                urls.add(url);
            }

            return Result.ok("上传成功", urls);
        } catch (IOException e) {
            return Result.fail("上传失败: " + e.getMessage());
        }
    }
}
