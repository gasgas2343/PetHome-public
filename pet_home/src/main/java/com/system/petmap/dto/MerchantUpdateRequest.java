package com.system.petmap.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MerchantUpdateRequest {

    @NotBlank
    private String merchantName;

    private String merchantType;

    private String description;

    private String phone;

    private String email;

    private String city;

    private String district;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String businessHours;

    private String imageUrl;

    private String status;
}
