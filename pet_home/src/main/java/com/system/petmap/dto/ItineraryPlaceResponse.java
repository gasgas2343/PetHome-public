package com.system.petmap.dto;

import java.math.BigDecimal;

import lombok.Data;



@Data
public class ItineraryPlaceResponse {

    private Integer placeId;

    private String name;

    private Integer dayNo;

    private Integer sequenceNo;

    private Integer transportTime;

    private BigDecimal latitude;

    private BigDecimal longitude;
}