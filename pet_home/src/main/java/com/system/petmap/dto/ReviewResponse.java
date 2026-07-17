package com.system.petmap.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReviewResponse {

    private Integer reviewId;

    private Long userId;

    private String userName;

    private Integer placeId;

    private Byte ratingOverall;

    private Byte ratingPetFriendly;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
