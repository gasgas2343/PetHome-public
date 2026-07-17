package com.system.petmap.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "map_place_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceTag {

    @EmbeddedId
    private PlaceTagId id;
}