package com.system.petmap.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItineraryPlaceCreateRequest {

    @NotNull
    private Integer placeId;

    @NotNull
    private Integer dayNo;
     // 後端自動計算
    private Integer sequenceNo;

    private Integer transportTime;
}
