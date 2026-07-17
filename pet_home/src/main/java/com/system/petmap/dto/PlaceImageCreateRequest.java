package com.system.petmap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceImageCreateRequest {

    private Integer placeId;

    private String imageUrl;

    private Integer sortOrder;

}