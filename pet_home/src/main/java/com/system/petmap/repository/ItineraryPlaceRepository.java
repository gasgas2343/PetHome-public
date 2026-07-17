package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.ItineraryPlace;
import com.system.petmap.entity.ItineraryPlaceId;

public interface ItineraryPlaceRepository
                extends JpaRepository<ItineraryPlace, ItineraryPlaceId> {

        List<ItineraryPlace> findByIdItineraryIdOrderByDayNoAscSequenceNoAsc(
                        Integer itineraryId);

        void deleteByIdItineraryId(
                        Integer itineraryId);

        List<ItineraryPlace> findByIdItineraryIdAndDayNoOrderBySequenceNoAsc(
                        Integer itineraryId,
                        Integer dayNo);
}