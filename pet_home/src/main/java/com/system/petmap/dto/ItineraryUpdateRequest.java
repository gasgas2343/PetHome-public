package com.system.petmap.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItineraryUpdateRequest {

    @NotBlank(message = "行程名稱不能空白")
    private String title;
}
