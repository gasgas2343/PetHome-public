package com.system.petmap.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.system.petmap.dto.FavoriteCreateRequest;
import com.system.petmap.dto.FavoriteResponse;
import com.system.petmap.entity.Favorite;
import com.system.petmap.entity.FavoriteId;
import com.system.petmap.entity.Place;
import com.system.petmap.entity.PlaceImage;
import com.system.member.exception.BusinessException;
import com.system.petmap.repository.FavoriteRepository;
import com.system.petmap.repository.PlaceImageRepository;
import com.system.petmap.repository.PlaceRepository;
import com.system.petmap.repository.PlaceTagRepository;
import com.system.petmap.repository.PetMapTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {

        private final FavoriteRepository favoriteRepository;

        private final PlaceRepository placeRepository;

        private final PlaceImageRepository placeImageRepository;

        private final PlaceTagRepository placeTagRepository;

        private final PetMapTagRepository tagRepository;

        public Favorite create(
                        FavoriteCreateRequest request) {

                placeRepository.findById(request.getPlaceId())
                                .orElseThrow(() -> new BusinessException(
                                                "PLACE_NOT_FOUND",
                                                "找不到景點"));

                FavoriteId id = new FavoriteId();

                id.setUserId(request.getUserId());

                id.setPlaceId(request.getPlaceId());

                if (favoriteRepository.existsById(id)) {
                        throw new BusinessException(
                                        "FAVORITE_ALREADY_EXISTS",
                                        "已收藏過此地點");
                }

                Favorite favorite = new Favorite();

                favorite.setId(id);

                favorite.setCreatedAt(
                                LocalDateTime.now());

                return favoriteRepository.save(
                                favorite);
        }

        public List<FavoriteResponse> findByUserId(
                        Long userId) {

                List<Favorite> favorites = favoriteRepository.findByIdUserId(userId);

                List<FavoriteResponse> responses = new ArrayList<>();

                for (Favorite favorite : favorites) {

                        Integer placeId = favorite.getId().getPlaceId();

                        Place place = placeRepository.findById(placeId)
                                        .orElseThrow(() -> new BusinessException(
                                                        "PLACE_NOT_FOUND",
                                                        "找不到景點"));


                        FavoriteResponse response = new FavoriteResponse();

                        response.setPlaceId(
                                        place.getPlaceId());

                        response.setName(
                                        place.getName());

                        response.setPlaceType(
                                        place.getPlaceType().name());

                        response.setAddress(
                                        place.getAddress());

                        response.setRatingAvg(
                                        place.getRatingAvg());

                        response.setReviewCount(
                                        place.getReviewCount());

                        List<String> tagNames = placeTagRepository
                                        .findByIdPlaceId(placeId)
                                        .stream()
                                        .map(placeTag -> tagRepository
                                                        .findById(
                                                                        placeTag.getId().getTagId())
                                                        .map(tag -> tag.getName())
                                                        .orElse(null))
                                        .filter(Objects::nonNull)
                                        .toList();

                        response.setTags(tagNames);

                        List<PlaceImage> images = placeImageRepository
                                        .findByPlaceIdOrderBySortOrderAsc(
                                                        placeId);

                        if (!images.isEmpty()) {

                                response.setCoverImageUrl(
                                                images.get(0).getImageUrl());
                        }

                        responses.add(response);
                }

                return responses;
        }

        public void delete(
                        Long userId,
                        Integer placeId) {

                FavoriteId id = new FavoriteId();

                id.setUserId(userId);

                id.setPlaceId(placeId);

                Favorite favorite = favoriteRepository
                                .findById(id)
                                .orElseThrow(() -> new BusinessException(
                                                "FAVORITE_NOT_FOUND",
                                                "找不到收藏"));

                favoriteRepository.delete(favorite);


        }

        public boolean exists(
                        Long userId,
                        Integer placeId) {

                FavoriteId id = new FavoriteId();

                id.setUserId(
                                userId);

                id.setPlaceId(
                                placeId);

                return favoriteRepository
                                .existsById(id);
        }
}