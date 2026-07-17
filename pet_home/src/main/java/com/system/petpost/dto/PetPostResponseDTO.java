
package com.system.petpost.dto;

import java.time.LocalDate;

import lombok.Data;

/**
 * 寵物時間軸回傳 DTO
 */
@Data
public class PetPostResponseDTO {

    // 時間軸貼文ID
    private Long petPostId;

    // 毛孩ID
    private Long petId;

    // 標題
    private String title;

    // 內容
    private String content;

    // 日期
    private LocalDate postDate;

    // Cloudinary圖片網址
    private String imageUrl;
}