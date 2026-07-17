package com.system.petmap.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "map_itinerary_places")
public class ItineraryPlace {

    @EmbeddedId
    private ItineraryPlaceId id;

    @Column(name = "day_no")
    private Integer dayNo;

    @Column(name = "sequence_no")
    private Integer sequenceNo;

    @Column(name = "transport_time")
    private Integer transportTime;
}