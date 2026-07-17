package com.system.petmap.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlaceSubmissionCreateRequest {

    @NotNull
    private Long  userId;

    @NotBlank(message = "地點名稱不能空白")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "地點類型不能空白")
    private String placeType;

    @NotBlank(message = "地址不能空白")
    @Size(max = 255)
    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String phone;

    @Size(max = 1000)
    private String description;

    private List<Integer> tagIds;
}