package com.system.petmap.dto;

import lombok.Data;

@Data
public class ReviewCreateRequest {

    private Long  userId;

    private Integer placeId;

    private Byte ratingOverall;

    private Byte ratingPetFriendly;

    private String title;

    private String content;
}