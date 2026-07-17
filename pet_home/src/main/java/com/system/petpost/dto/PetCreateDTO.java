package com.system.petpost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 建立寵物 DTO
@Data
public class PetCreateDTO {

    // 毛孩名稱
    @NotBlank(message = "毛孩名稱不可為空")
    @Size(
            min = 2,
            max = 50,
            message = "毛孩名稱需介於2~50字")
    private String petName;

    // 毛孩介紹
    @Size(
            max = 300,
            message = "毛孩介紹不可超過300字")
    private String petIntro;

    // 毛孩頭像網址
    @Size(
            max = 500,
            message = "圖片網址不可超過500字")
    private String petAvatarUrl;

}