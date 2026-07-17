package com.system.petpost.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 修改文章 DTO
@Data
public class PostUpdateDTO {
    // 分類編號
    @NotNull(message = "文章分類不可為空")
    private Long categoryId;

    // 文章標題
    @NotBlank(message = "文章標題不可為空")
    @Size(min = 2, max = 200, message = "文章標題需介於2~200字")
    private String title;

    // 文章內容
    @NotBlank(message = "文章內容不可為空")
    @Size(min = 10, max = 5000, message = "文章內容需介於10~5000字")
    private String content;

    // Cloudinary 封面圖片網址
    @Size(max = 500, message = "圖片網址不可超過500字")
    private String coverImageUrl;

    // 前端可傳多張 Cloudinary 圖片網址；目前先存第一張為 coverImageUrl
    private List<String> imageUrls;

    // 文章標籤
    private List<String> tags;
}
