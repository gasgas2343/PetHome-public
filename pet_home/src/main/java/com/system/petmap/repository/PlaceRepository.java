package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.Place;


import com.system.petmap.model.PlaceType;

public interface PlaceRepository
        extends JpaRepository<Place, Integer> {

    List<Place> findByNameContaining(String keyword);

    List<Place> findByPlaceType(PlaceType placeType);

    List<Place> findByStatus(String status);

}
