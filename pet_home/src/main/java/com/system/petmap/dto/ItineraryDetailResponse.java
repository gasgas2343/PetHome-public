package com.system.petmap.dto;

import java.util.List;

import lombok.Data;

@Data
public class ItineraryDetailResponse {

    private Integer itineraryId;

    private String title;

    private Long userId;

    private List<ItineraryPlaceResponse> places;
}