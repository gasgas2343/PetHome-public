package com.system.petmap.dto;

import lombok.Data;

@Data
public class ItineraryPlaceReorderRequest {

    private Integer placeId;

    private Integer dayNo;

    private Integer sequenceNo;

}