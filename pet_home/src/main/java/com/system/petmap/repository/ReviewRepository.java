package com.system.petmap.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.system.petmap.entity.Review;

public interface ReviewRepository
        extends JpaRepository<Review, Integer> {

    List<Review> findByPlaceId(Integer placeId);

    long countByPlaceId(Integer placeId);

    Optional<Review> findByUserIdAndPlaceId(
            Long userId,
            Integer placeId);

    List<Review> findByPlaceIdOrderByCreatedAtDesc(
            Integer placeId);

    List<Review> findByPlaceIdOrderByRatingOverallDesc(
            Integer placeId);

    List<Review> findByPlaceIdOrderByRatingOverallAsc(
            Integer placeId);

    List<Review> findByPlaceIdOrderByRatingPetFriendlyDesc(
            Integer placeId);

    List<Review> findByPlaceIdOrderByRatingPetFriendlyAsc(
            Integer placeId);

    @Query("""
            SELECT AVG(r.ratingOverall)
            FROM Review r
            WHERE r.placeId = :placeId
            """)
    BigDecimal getAverageOverallRating(
            Integer placeId);

    @Query("""
            SELECT AVG(r.ratingPetFriendly)
            FROM Review r
            WHERE r.placeId = :placeId
            """)
    BigDecimal getAveragePetFriendlyRating(
            Integer placeId);
            
}
