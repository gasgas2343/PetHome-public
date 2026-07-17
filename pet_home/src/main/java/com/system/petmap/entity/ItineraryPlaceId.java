package com.system.petmap.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ItineraryPlaceId
        implements Serializable {

    private Integer itineraryId;

    private Integer placeId;
}
