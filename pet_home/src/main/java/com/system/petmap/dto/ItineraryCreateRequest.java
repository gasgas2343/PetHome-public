package com.system.petmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItineraryCreateRequest {

    @NotNull
    private Long userId;

    @NotBlank(message = "行程名稱不能空白")
    private String title;

}
