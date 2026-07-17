package com.system.petmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportCreateRequest {

    @NotNull
    private Long reporterId;

    private Integer placeId;

    private Integer reviewId;

    @NotBlank(message = "檢舉原因不能空白")
    private String reason;

    @Size(max = 1000)
    private String description;
}
