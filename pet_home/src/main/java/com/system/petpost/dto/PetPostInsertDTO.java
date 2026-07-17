package com.system.petpost.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增寵物時間軸 DTO
 */
@Data
public class PetPostInsertDTO {

    // 毛孩ID
    @NotNull(message = "毛孩ID不可為空")
    private Long petId;

    // 標題
    @NotBlank(message = "標題不可空白")
    @Size(max = 100, message = "標題不可超過100字")
    private String title;

    // 內容
    @Size(max = 1000, message = "內容不可超過1000字")
    private String content;

    // 時間軸日期
    @NotNull(message = "日期不可空白")
    private LocalDate postDate;

    // Cloudinary圖片網址
    @Size(max = 500, message = "圖片網址不可超過500字")
    private String imageUrl;
}