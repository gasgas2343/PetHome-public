package com.system.petmap.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.PlaceRatingSummaryResponse;
import com.system.petmap.dto.ReviewCreateRequest;
import com.system.petmap.dto.ReviewUpdateRequest;
import com.system.petmap.entity.Review;
import com.system.petmap.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map/reviews")
@RequiredArgsConstructor
public class ReviewController {

        private final ReviewService reviewService;

        @RequirePermission("REVIEW_CREATE")
        @PostMapping
        public ApiResponse<Review> create(
                        @RequestBody ReviewCreateRequest request) {

                return ApiResponse.success(
                                "REVIEW_CREATE_SUCCESS",
                                "新增評論成功",
                                reviewService.create(request));
        }

        @RequirePermission("REVIEW_LIST_BY_PLACE")
        @GetMapping("/place/{placeId}")
        public ApiResponse<List<Review>> getByPlaceId(
                        @PathVariable Integer placeId,
                        @RequestParam(defaultValue = "latest") String sort) {

                return ApiResponse.success(
                                "REVIEW_LIST_SUCCESS",
                                "取得評論成功",
                                reviewService.getReviewsByPlaceId(
                                                placeId,
                                                sort));
        }

        @RequirePermission("REVIEW_UPDATE")
        @PutMapping("/{reviewId}")
        public ApiResponse<Review> update(
                        @PathVariable Integer reviewId,
                        @RequestBody ReviewUpdateRequest request) {

                return ApiResponse.success(
                                "REVIEW_UPDATE_SUCCESS",
                                "修改評論成功",
                                reviewService.update(
                                                reviewId,
                                                request));
        }

        @RequirePermission("REVIEW_DELETE")
        @DeleteMapping("/{reviewId}")
        public ApiResponse<Void> delete(
                        @PathVariable Integer reviewId) {

                reviewService.delete(reviewId);

                return ApiResponse.success(
                                "REVIEW_DELETE_SUCCESS",
                                "刪除評論成功",
                                null);
        }

        @RequirePermission("PLACE_RATING_SUMMARY")
        @GetMapping("/place/{placeId}/summary")
        public ApiResponse<PlaceRatingSummaryResponse> getSummary(
                        @PathVariable Integer placeId) {

                return ApiResponse.success(
                                "PLACE_RATING_SUMMARY_SUCCESS",
                                "取得評分統計成功",
                                reviewService.getRatingSummary(placeId));
        }
}
