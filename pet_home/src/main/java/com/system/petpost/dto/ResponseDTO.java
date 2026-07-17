package com.system.petpost.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ResponseDTO {

    // 時間軸ID
    private Long petPostId;

    // 標題
    private String title;

    // 內容
    private String content;

    // 日期
    private LocalDate postDate;

    // Cloudinary圖片網址
    private String imageUrl;

}