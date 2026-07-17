package com.system.petmap.dto;

import lombok.Data;

@Data
public class FavoriteCreateRequest {

    private Long  userId;

    private Integer placeId;
}