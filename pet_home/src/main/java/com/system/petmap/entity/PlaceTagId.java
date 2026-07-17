package com.system.petmap.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceTagId implements Serializable {

    @Column(name = "place_id")
    private Integer placeId;

    @Column(name = "tag_id")
    private Integer tagId;
}