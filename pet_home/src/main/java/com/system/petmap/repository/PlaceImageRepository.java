package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.PlaceImage;

public interface PlaceImageRepository
        extends JpaRepository<PlaceImage, Integer> {

    List<PlaceImage> findByPlaceIdOrderBySortOrderAsc(
            Integer placeId);

}