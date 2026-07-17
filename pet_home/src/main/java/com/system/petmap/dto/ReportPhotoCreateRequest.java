package com.system.petmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportPhotoCreateRequest {

    @NotNull
    private Integer reportId;

    @NotBlank(message = "圖片網址不能空白")
    private String imageUrl;
}
