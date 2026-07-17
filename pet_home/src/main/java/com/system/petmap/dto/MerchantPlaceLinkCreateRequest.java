package com.system.petmap.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantPlaceLinkCreateRequest {

    @NotNull
    private Long merchantId;

    @NotNull
    private Integer placeId;
}
