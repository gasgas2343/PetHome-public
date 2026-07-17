package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.system.petmap.entity.PlaceTag;
import com.system.petmap.entity.PlaceTagId;

import jakarta.transaction.Transactional;

public interface PlaceTagRepository
        extends JpaRepository<PlaceTag, PlaceTagId> {

    List<PlaceTag> findByIdPlaceId(Integer placeId);

    List<PlaceTag> findByIdTagId(Integer tagId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PlaceTag pt WHERE pt.id.placeId = :placeId")
    void deleteByPlaceId(Integer placeId);

}
