package com.system.petmap.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class FavoriteResponse {

    private Integer placeId;

    private String name;

    private String placeType;

    private String address;

    private BigDecimal ratingAvg;

    private Integer reviewCount;

    private String coverImageUrl;

    private List<String> tags;
}