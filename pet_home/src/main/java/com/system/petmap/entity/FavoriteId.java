package com.system.petmap.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class FavoriteId implements Serializable {

    private Long userId;

    private Integer placeId;
}
