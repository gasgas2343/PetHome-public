package com.system.petmap.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.ReviewImageCreateRequest;
import com.system.petmap.entity.ReviewImage;
import com.system.petmap.service.ReviewImageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map/review-images")
@RequiredArgsConstructor
public class ReviewImageController {

        private final ReviewImageService reviewImageService;

        @RequirePermission("REVIEW_IMAGE_LIST")
        @GetMapping("/review/{reviewId}")
        public ApiResponse<List<ReviewImage>> getByReviewId(
                        @PathVariable Integer reviewId) {

                return ApiResponse.success(
                                "REVIEW_IMAGE_LIST_SUCCESS",
                                "取得評論圖片成功",
                                reviewImageService.getByReviewId(reviewId));
        }

        @RequirePermission("REVIEW_IMAGE_CREATE")
        @PostMapping
        public ApiResponse<ReviewImage> create(
                        @RequestBody ReviewImageCreateRequest request) {

                return ApiResponse.success(
                                "REVIEW_IMAGE_CREATE_SUCCESS",
                                "新增評論圖片成功",
                                reviewImageService.create(request));
        }

        @RequirePermission("REVIEW_IMAGE_DELETE")
        @DeleteMapping("/{imageId}")
        public ApiResponse<Void> delete(
                        @PathVariable Integer imageId) {

                reviewImageService.delete(imageId);

                return ApiResponse.success(
                                "REVIEW_IMAGE_DELETE_SUCCESS",
                                "刪除評論圖片成功",
                                null);
        }
}