package com.system.petmap.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class PlaceSearchResponse {

    private Integer placeId;

    private String name;

    private String placeType;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private BigDecimal ratingAvg;

    private BigDecimal ratingPetFriendly;

    private Integer reviewCount;

    private List<String> tags;

    private String coverImageUrl;
}
