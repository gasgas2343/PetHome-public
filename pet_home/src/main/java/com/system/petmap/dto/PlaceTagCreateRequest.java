package com.system.petmap.dto;

import lombok.Data;

@Data
public class PlaceTagCreateRequest {

    private Integer placeId;

    private Integer tagId;
}
