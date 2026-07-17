package com.system.petmap.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.exception.BusinessException;
import com.system.petmap.dto.ItineraryCreateRequest;
import com.system.petmap.dto.ItineraryDetailResponse;
import com.system.petmap.dto.ItineraryPlaceCreateRequest;
import com.system.petmap.dto.ItineraryPlaceReorderRequest;
import com.system.petmap.dto.ItineraryPlaceResponse;
import com.system.petmap.dto.ItineraryUpdateRequest;
import com.system.petmap.entity.Itinerary;
import com.system.petmap.entity.ItineraryPlace;
import com.system.petmap.entity.ItineraryPlaceId;
import com.system.petmap.entity.Place;
import com.system.petmap.repository.ItineraryPlaceRepository;
import com.system.petmap.repository.ItineraryRepository;
import com.system.petmap.repository.PlaceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItineraryService {

        private final ItineraryRepository itineraryRepository;

        private final ItineraryPlaceRepository itineraryPlaceRepository;

        private final PlaceRepository placeRepository;

        public Itinerary create(
                        ItineraryCreateRequest request) {

                Itinerary itinerary = new Itinerary();

                itinerary.setUserId(
                                request.getUserId());

                itinerary.setTitle(
                                request.getTitle());

                LocalDateTime now = LocalDateTime.now();

                itinerary.setCreatedAt(now);
                itinerary.setUpdatedAt(now);

                return itineraryRepository.save(
                                itinerary);
        }

        public List<Itinerary> findByUserId(
                        Long userId) {

                return itineraryRepository
                                .findByUserId(userId);
        }

        public ItineraryPlace addPlace(
                        Integer itineraryId,
                        ItineraryPlaceCreateRequest request) {

                placeRepository.findById(request.getPlaceId())
                                .orElseThrow(() -> new BusinessException(
                                                "PLACE_NOT_FOUND",
                                                "找不到景點"));

                Itinerary itinerary = itineraryRepository
                                .findById(itineraryId)
                                .orElseThrow(() -> new BusinessException(
                                                "ITINERARY_NOT_FOUND",
                                                "找不到行程"));

                ItineraryPlaceId id = new ItineraryPlaceId();

                id.setItineraryId(
                                itinerary.getItineraryId());

                id.setPlaceId(
                                request.getPlaceId());

                ItineraryPlace itineraryPlace = new ItineraryPlace();

                itineraryPlace.setId(id);

                itineraryPlace.setDayNo(
                                request.getDayNo());

                List<ItineraryPlace> places = itineraryPlaceRepository
                                .findByIdItineraryIdAndDayNoOrderBySequenceNoAsc(
                                                itineraryId,
                                                request.getDayNo());

                itineraryPlace.setSequenceNo(
                                places.size() + 1);

                itineraryPlace.setTransportTime(
                                request.getTransportTime());

                return itineraryPlaceRepository
                                .save(itineraryPlace);
        }

        public ItineraryDetailResponse getDetail(
                        Integer itineraryId) {

                Itinerary itinerary = itineraryRepository
                                .findById(itineraryId)
                                .orElseThrow(() -> new BusinessException(
                                                "ITINERARY_NOT_FOUND",
                                                "找不到行程"));

                List<ItineraryPlace> itineraryPlaces = itineraryPlaceRepository
                                .findByIdItineraryIdOrderByDayNoAscSequenceNoAsc(
                                                itineraryId);

                ItineraryDetailResponse response = new ItineraryDetailResponse();

                response.setItineraryId(
                                itinerary.getItineraryId());

                response.setTitle(
                                itinerary.getTitle());

                response.setUserId(
                                itinerary.getUserId());

                List<ItineraryPlaceResponse> placeResponses = new ArrayList<>();

                for (ItineraryPlace itineraryPlace : itineraryPlaces) {

                        Integer placeId = itineraryPlace.getId()
                                        .getPlaceId();

                        Place place = placeRepository
                                        .findById(placeId)
                                        .orElseThrow(() -> new BusinessException(
                                                        "PLACE_NOT_FOUND",
                                                        "找不到景點"));

                        ItineraryPlaceResponse placeResponse = new ItineraryPlaceResponse();

                        placeResponse.setPlaceId(
                                        place.getPlaceId());

                        placeResponse.setName(
                                        place.getName());

                        placeResponse.setDayNo(
                                        itineraryPlace.getDayNo());

                        placeResponse.setSequenceNo(
                                        itineraryPlace.getSequenceNo());

                        placeResponse.setTransportTime(
                                        itineraryPlace.getTransportTime());

                        placeResponse.setLatitude(
                                        place.getLatitude());

                        placeResponse.setLongitude(
                                        place.getLongitude());

                        placeResponses.add(
                                        placeResponse);
                }
                response.setPlaces(
                                placeResponses);

                return response;

        }

        public Itinerary update(
                        Integer itineraryId,
                        ItineraryUpdateRequest request) {

                Itinerary itinerary = itineraryRepository
                                .findById(itineraryId)
                                .orElseThrow(() -> new BusinessException(
                                                "ITINERARY_NOT_FOUND",
                                                "找不到行程"));

                itinerary.setTitle(
                                request.getTitle());

                LocalDateTime now = LocalDateTime.now();

                itinerary.setUpdatedAt(now);

                return itineraryRepository.save(
                                itinerary);
        }

        @Transactional
        public void delete(
                        Integer itineraryId) {

                Itinerary itinerary = itineraryRepository
                                .findById(itineraryId)
                                .orElseThrow(() -> new BusinessException(
                                                "ITINERARY_NOT_FOUND",
                                                "找不到行程"));

                itineraryPlaceRepository
                                .deleteByIdItineraryId(
                                                itineraryId);

                itineraryRepository.delete(
                                itinerary);
        }

        @Transactional
        public void removePlace(
                        Integer itineraryId,
                        Integer placeId) {
                ItineraryPlaceId id = new ItineraryPlaceId();

                id.setItineraryId(
                                itineraryId);

                id.setPlaceId(
                                placeId);
                ItineraryPlace itineraryPlace = itineraryPlaceRepository
                                .findById(id)
                                .orElseThrow(() -> new BusinessException(
                                                "ITINERARY_PLACE_NOT_FOUND",
                                                "找不到行程景點"));

                itineraryPlaceRepository.delete(itineraryPlace);
        }

        @Transactional
        public void reorder(
                        Integer itineraryId,
                        List<ItineraryPlaceReorderRequest> requests) {

                for (ItineraryPlaceReorderRequest request : requests) {

                        ItineraryPlaceId id = new ItineraryPlaceId();

                        id.setItineraryId(itineraryId);
                        id.setPlaceId(request.getPlaceId());

                        ItineraryPlace itineraryPlace = itineraryPlaceRepository
                                        .findById(id)
                                        .orElseThrow(() -> new BusinessException(
                                                        "ITINERARY_PLACE_NOT_FOUND",
                                                        "找不到行程景點"));

                        itineraryPlace.setDayNo(
                                        request.getDayNo());

                        itineraryPlace.setSequenceNo(
                                        request.getSequenceNo());

                        itineraryPlaceRepository.save(
                                        itineraryPlace);
                }
        }

}
