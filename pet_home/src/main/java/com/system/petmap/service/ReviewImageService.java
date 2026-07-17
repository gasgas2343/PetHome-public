package com.system.petmap.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.exception.BusinessException;
import com.system.petmap.dto.ReviewImageCreateRequest;
import com.system.petmap.entity.ReviewImage;
import com.system.petmap.repository.ReviewImageRepository;
import com.system.petmap.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewImageService {

        private final ReviewImageRepository reviewImageRepository;

        private final ReviewRepository reviewRepository;

        public List<ReviewImage> getByReviewId(
                        Integer reviewId) {

                return reviewImageRepository
                                .findByReviewIdOrderBySortOrderAsc(
                                                reviewId);
        }

        public ReviewImage create(
                        ReviewImageCreateRequest request) {

                reviewRepository.findById(request.getReviewId())
                                .orElseThrow(() -> new BusinessException(
                                                "REVIEW_NOT_FOUND",
                                                "找不到評論"));

                ReviewImage image = new ReviewImage();

                image.setReviewId(request.getReviewId());
                image.setImageUrl(request.getImageUrl());
                image.setSortOrder(request.getSortOrder());

                LocalDateTime now = LocalDateTime.now();
                image.setCreatedAt(now);

                return reviewImageRepository.save(image);
        }

        public void delete(Integer imageId) {

                ReviewImage image = reviewImageRepository
                                .findById(imageId)
                                .orElseThrow(() -> new BusinessException(
                                                "REVIEW_IMAGE_NOT_FOUND",
                                                "找不到評論圖片"));

                reviewImageRepository.delete(
                                image);
        }
}
