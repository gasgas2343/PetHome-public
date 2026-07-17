package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.Favorite;
import com.system.petmap.entity.FavoriteId;


public interface FavoriteRepository
        extends JpaRepository<Favorite, FavoriteId> {

    List<Favorite> findByIdUserId(Long userId);

    List<Favorite> findByIdPlaceId(Integer placeId);

    boolean existsById(
        FavoriteId id);
}
