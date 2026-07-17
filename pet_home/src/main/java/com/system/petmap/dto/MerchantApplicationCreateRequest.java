package com.system.petmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantApplicationCreateRequest {

    @NotNull
    private Long userId;

    @NotBlank(message = "商家名稱不能空白")
    private String merchantName;

    private String taxId;

    private String businessLicenseUrl;

    private String placeName;

    private String placeAddress;
}
