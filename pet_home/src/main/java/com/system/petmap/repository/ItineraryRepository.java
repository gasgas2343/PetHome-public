package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.Itinerary;

public interface ItineraryRepository
        extends JpaRepository<Itinerary, Integer> {

    List<Itinerary> findByUserId(
            Long userId);
}
