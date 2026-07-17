package com.system.petmap.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.exception.BusinessException;
import com.system.petmap.dto.PlaceImageCreateRequest;
import com.system.petmap.entity.PlaceImage;
import com.system.petmap.repository.PlaceImageRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceImageService {

        private final PlaceImageRepository placeImageRepository;

        public List<PlaceImage> findByPlaceId(
                        Integer placeId) {

                return placeImageRepository
                                .findByPlaceIdOrderBySortOrderAsc(
                                                placeId);
        }

        public PlaceImage create(
                        PlaceImageCreateRequest request) {

                PlaceImage image = new PlaceImage();

                image.setPlaceId(
                                request.getPlaceId());

                image.setImageUrl(
                                request.getImageUrl());

                image.setSortOrder(
                                request.getSortOrder());

                return placeImageRepository
                                .save(image);
        }

        public void delete(
                        Integer imageId) {

                PlaceImage image = placeImageRepository
                                .findById(imageId)
                                .orElseThrow(() -> new BusinessException(
                                                "PLACE_IMAGE_NOT_FOUND",
                                                "找不到景點圖片"));

                placeImageRepository.delete(image);
        }

}