package com.system.petmap.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.member.entity.UserProfilesBean;
import com.system.member.exception.BusinessException;
import com.system.member.repository.UserProfileRepository;
import com.system.member.repository.UsersRepository;
import com.system.petmap.dto.PlaceRatingSummaryResponse;
import com.system.petmap.dto.ReviewCreateRequest;
import com.system.petmap.dto.ReviewUpdateRequest;
import com.system.petmap.entity.Place;
import com.system.petmap.entity.Review;
import com.system.petmap.repository.PlaceRepository;
import com.system.petmap.repository.ReviewImageRepository;
import com.system.petmap.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class ReviewService {

        private final UserProfileRepository userProfileRepository;

        private final UsersRepository usersRepository;

        private final ReviewRepository reviewRepository;

        private final PlaceRepository placeRepository;

        private final ReviewImageRepository reviewImageRepository;

        public Review create(
                        ReviewCreateRequest request) {

                placeRepository.findById(request.getPlaceId())
                                .orElseThrow(() -> new BusinessException(
                                                "PLACE_NOT_FOUND",
                                                "找不到景點"));

                Optional<Review> existingReview = reviewRepository.findByUserIdAndPlaceId(
                                request.getUserId(),
                                request.getPlaceId());

                if (existingReview.isPresent()) {

                        throw new BusinessException(
                                        "REVIEW_ALREADY_EXISTS",
                                        "您已經評論過這個地點");
                }

                Review review = new Review();

                review.setUserId(request.getUserId());

                review.setPlaceId(request.getPlaceId());

                review.setRatingOverall(
                                request.getRatingOverall());

                review.setRatingPetFriendly(
                                request.getRatingPetFriendly());

                review.setTitle(request.getTitle());

                review.setContent(request.getContent());

                review.setCreatedAt(LocalDateTime.now());

                review.setUpdatedAt(LocalDateTime.now());

                Review saved = reviewRepository.save(review);

                updatePlaceRating(
                                request.getPlaceId());

                return saved;
        }

        public List<Review> findByPlaceId(
                        Integer placeId) {

                return reviewRepository.findByPlaceId(
                                placeId);
        }

        private void updatePlaceRating(Integer placeId) {

                List<Review> reviews = reviewRepository.findByPlaceId(placeId);

                if (reviews.isEmpty()) {
                        return;
                }

                double avg = reviews.stream()
                                .mapToInt(
                                                r -> r.getRatingOverall())
                                .average()
                                .orElse(0);

                Place place = placeRepository.findById(placeId)
                                .orElse(null);

                if (place == null) {
                        return;
                }

                place.setRatingAvg(
                                BigDecimal.valueOf(avg));

                place.setReviewCount(
                                reviews.size());

                placeRepository.save(place);
        }

        public Review update(
                        Integer reviewId,
                        ReviewUpdateRequest request) {

                Review review = reviewRepository.findById(reviewId)
                                .orElseThrow(() -> new BusinessException(
                                                "REVIEW_NOT_FOUND",
                                                "找不到評論"));

                review.setRatingOverall(
                                request.getRatingOverall());

                review.setRatingPetFriendly(
                                request.getRatingPetFriendly());

                review.setTitle(
                                request.getTitle());

                review.setContent(
                                request.getContent());

                review.setUpdatedAt(
                                LocalDateTime.now());

                Review saved = reviewRepository.save(review);

                updatePlaceRating(
                                review.getPlaceId());

                return saved;
        }

        public void delete(Integer reviewId) {

                Review review = reviewRepository.findById(reviewId)
                                .orElseThrow(() -> new BusinessException(
                                                "REVIEW_NOT_FOUND",
                                                "找不到評論"));

                Integer placeId = review.getPlaceId();

                reviewImageRepository.deleteByReviewId(reviewId);

                reviewRepository.delete(review);

                updatePlaceRating(placeId);

        }

        public List<Review> getReviewsByPlaceId(
                        Integer placeId,
                        String sort) {

                List<Review> reviews;

                if ("rating_desc".equals(sort)) {

                        reviews = reviewRepository
                                        .findByPlaceIdOrderByRatingOverallDesc(placeId);

                } else if ("rating_asc".equals(sort)) {

                        reviews = reviewRepository
                                        .findByPlaceIdOrderByRatingOverallAsc(placeId);

                } else if ("pet_desc".equals(sort)) {

                        reviews = reviewRepository
                                        .findByPlaceIdOrderByRatingPetFriendlyDesc(placeId);

                } else if ("pet_asc".equals(sort)) {

                        reviews = reviewRepository
                                        .findByPlaceIdOrderByRatingPetFriendlyAsc(placeId);

                } else {

                        reviews = reviewRepository
                                        .findByPlaceIdOrderByCreatedAtDesc(placeId);
                }

                for (Review review : reviews) {

                        UserProfilesBean profile = userProfileRepository
                                        .findByUserId(review.getUserId())
                                        .orElse(null);

                        if (profile == null) {
                                review.setUserName("未知使用者");
                        } else if (profile.getNickname() != null && !profile.getNickname().isBlank()) {
                                review.setUserName(profile.getNickname());
                        } else if (profile.getFullName() != null && !profile.getFullName().isBlank()) {
                                review.setUserName(profile.getFullName());
                        } else {
                                review.setUserName("未知使用者");
                        }
                }

                return reviews;
        }

        public PlaceRatingSummaryResponse getRatingSummary(
                        Integer placeId) {

                BigDecimal overall = reviewRepository
                                .getAverageOverallRating(
                                                placeId);

                BigDecimal pet = reviewRepository
                                .getAveragePetFriendlyRating(
                                                placeId);

                long reviewCount = reviewRepository
                                .countByPlaceId(
                                                placeId);

                return new PlaceRatingSummaryResponse(
                                overall,
                                pet,
                                reviewCount);
        }

}
