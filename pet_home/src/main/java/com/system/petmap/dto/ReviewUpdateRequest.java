package com.system.petmap.dto;

import lombok.Data;

@Data
public class ReviewUpdateRequest {

    private Byte ratingOverall;

    private Byte ratingPetFriendly;

    private String title;

    private String content;
}
