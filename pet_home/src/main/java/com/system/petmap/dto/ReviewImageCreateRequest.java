package com.system.petmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewImageCreateRequest {

    @NotNull
    private Integer reviewId;

    @NotBlank
    private String imageUrl;

    private Integer sortOrder;
}