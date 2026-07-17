package com.system.petmap.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.exception.BusinessException;
import com.system.petmap.dto.PlaceCreateRequest;
import com.system.petmap.dto.PlaceSearchResponse;
import com.system.petmap.dto.PlaceUpdateRequest;
import com.system.petmap.entity.Place;
import com.system.petmap.entity.PlaceImage;
import com.system.petmap.model.PlaceType;
import com.system.petmap.repository.PlaceImageRepository;
import com.system.petmap.repository.PlaceRepository;
import com.system.petmap.repository.PlaceTagRepository;
import com.system.petmap.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

        private final ReviewRepository reviewRepository;

        private final PlaceTagRepository placeTagRepository;

        private final PlaceRepository placeRepository;

        private final PlaceImageRepository placeImageRepository;

        private final PlaceTagService placeTagService;

        public List<Place> findAll() {
                return placeRepository.findAll();
        }

        public Place findById(Integer id) {
                return placeRepository.findById(id)
                                .orElseThrow(() -> new BusinessException(
                                                "PLACE_NOT_FOUND",
                                                "找不到景點"));
        }

        public Place create(PlaceCreateRequest request) {

                LocalDateTime now = LocalDateTime.now();
                Place place = new Place();

                place.setName(request.getName());
                place.setPlaceType(PlaceType.valueOf(request.getPlaceType()));
                place.setAddress(request.getAddress());
                place.setLatitude(request.getLatitude());
                place.setLongitude(request.getLongitude());
                place.setPhone(request.getPhone());

                place.setStatus("ACTIVE");
                place.setReviewCount(0);

                place.setCreatedAt(now);
                place.setUpdatedAt(now);

                return placeRepository.save(place);
        }

        public Place update(
                        Integer id,
                        PlaceUpdateRequest request) {

                Place place = placeRepository.findById(id)
                                .orElseThrow(() -> new BusinessException(
                                                "PLACE_NOT_FOUND",
                                                "找不到景點"));

                place.setName(request.getName());
                place.setPlaceType(PlaceType.valueOf(request.getPlaceType()));
                place.setAddress(request.getAddress());
                place.setLatitude(request.getLatitude());
                place.setLongitude(request.getLongitude());
                place.setPhone(request.getPhone());
                place.setDescription(request.getDescription());
                place.setStatus(request.getStatus());

                place.setUpdatedAt(LocalDateTime.now());

                return placeRepository.save(place);
        }

        public void delete(Integer id) {

                Place place = placeRepository.findById(id)
                                .orElseThrow(() -> new BusinessException(
                                                "PLACE_NOT_FOUND",
                                                "找不到景點"));

                // 先刪除地點標籤
                placeTagRepository.deleteByPlaceId(id);

                // 再刪除地點
                placeRepository.delete(place);
        }

        public List<Place> findByPlaceType(String placeType) {

                return placeRepository.findByPlaceType(
                                PlaceType.valueOf(placeType));

        }

        public List<Place> search(
                        String placeType,
                        List<Integer> tagIds,
                        String keyword) {

                List<Place> places = placeRepository.findAll();

                // keyword

                if (keyword != null &&
                                !keyword.isBlank()) {

                        places = places.stream()

                                        .filter(place ->

                                        place.getName()

                                                        .contains(
                                                                        keyword))

                                        .toList();
                }

                // tag

                if (

                tagIds != null &&

                                !tagIds.isEmpty()

                ) {

                        for (Integer tagId : tagIds) {

                                List<Place> tagPlaces =

                                                placeTagService
                                                                .findPlacesByTagId(
                                                                                tagId);

                                places = places.stream()

                                                .filter(
                                                                tagPlaces::contains)

                                                .toList();
                        }
                }

                // placeType

                if (placeType != null &&
                                !placeType.isBlank()) {

                        places = places.stream()

                                        .filter(place ->

                                        placeType
                                                        .equalsIgnoreCase(

                                                                        place.getPlaceType().name()))

                                        .toList();
                }

                return places;
        }

        public List<PlaceSearchResponse> convertToSearchResponse(
                        List<Place> places) {

                return places.stream()
                                .map(place -> {

                                        PlaceSearchResponse dto = new PlaceSearchResponse();

                                        dto.setPlaceId(place.getPlaceId());

                                        dto.setName(place.getName());

                                        List<PlaceImage> images =

                                                        placeImageRepository
                                                                        .findByPlaceIdOrderBySortOrderAsc(
                                                                                        place.getPlaceId());

                                        if (!images.isEmpty()) {

                                                dto.setCoverImageUrl(

                                                                images.get(0)
                                                                                .getImageUrl()

                                );
                                        }

                                        dto.setPlaceType(place.getPlaceType().name());

                                        dto.setAddress(place.getAddress());

                                        dto.setLatitude(place.getLatitude());

                                        dto.setLongitude(place.getLongitude());

                                        dto.setRatingAvg(place.getRatingAvg());

                                        dto.setRatingPetFriendly(
                                                        reviewRepository.getAveragePetFriendlyRating(
                                                                        place.getPlaceId()));

                                        dto.setReviewCount(place.getReviewCount());

                                        dto.setTags(
                                                        placeTagService.findTagNamesByPlaceId(
                                                                        place.getPlaceId()));

                                        return dto;
                                })
                                .toList();
        }

        public List<Place> searchByKeyword(
                        String keyword) {

                return placeRepository
                                .findByNameContaining(
                                                keyword);
        }

}
