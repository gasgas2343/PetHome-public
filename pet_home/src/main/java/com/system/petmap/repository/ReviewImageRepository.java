package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.ReviewImage;

public interface ReviewImageRepository
                extends JpaRepository<ReviewImage, Integer> {

        List<ReviewImage> findByReviewIdOrderBySortOrderAsc(
                        Integer reviewId);

        void deleteByReviewId(
                        Integer reviewId);
}