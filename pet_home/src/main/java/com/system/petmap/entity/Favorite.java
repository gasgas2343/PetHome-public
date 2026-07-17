package com.system.petmap.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "map_favorites")
public class Favorite {

    @EmbeddedId
    private FavoriteId id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
