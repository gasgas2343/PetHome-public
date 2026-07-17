package com.system.petmap.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRatingSummaryResponse {

    private BigDecimal averageOverall;

    private BigDecimal averagePetFriendly;

    private Long reviewCount;

}