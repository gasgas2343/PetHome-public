package com.system.petmap.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.MerchantUpdateRequest;
import com.system.petmap.entity.Merchant;
import com.system.petmap.entity.Review;
import com.system.petmap.service.MerchantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map/merchants")
@RequiredArgsConstructor
public class MerchantController {

        private final MerchantService merchantService;

        @RequirePermission("MERCHANT_USER_LIST")
        @GetMapping("/user/{userId}")
        public ApiResponse<List<Merchant>> findByOwnerUserId(
                        @PathVariable Long userId) {

                return ApiResponse.success(
                                "MERCHANT_LIST_SUCCESS",
                                "取得商家成功",
                                merchantService.findByOwnerUserId(userId));
        }

        @RequirePermission("MERCHANT_GET")
        @GetMapping("/{merchantId}")
        public ApiResponse<Merchant> getById(
                        @PathVariable Long merchantId) {

                return ApiResponse.success(
                                "MERCHANT_GET_SUCCESS",
                                "取得商家成功",
                                merchantService.getById(merchantId));
        }

        @RequirePermission("MERCHANT_UPDATE")
        @PutMapping("/{merchantId}")
        public ApiResponse<Merchant> update(
                        @PathVariable Long merchantId,
                        @Valid @RequestBody MerchantUpdateRequest request) {

                return ApiResponse.success(
                                "MERCHANT_UPDATE_SUCCESS",
                                "更新商家成功",
                                merchantService.update(
                                                merchantId,
                                                request));
        }

        @RequirePermission("MERCHANT_DEACTIVATE")
        @PutMapping("/{merchantId}/deactivate")
        public ApiResponse<Merchant> deactivate(
                        @PathVariable Long merchantId) {

                return ApiResponse.success(
                                "MERCHANT_DEACTIVATE_SUCCESS",
                                "停用商家成功",
                                merchantService.deactivate(merchantId));
        }

        @RequirePermission("MERCHANT_REVIEW_LIST")
        @GetMapping("/{merchantId}/reviews")
        public ApiResponse<List<Review>> getMerchantReviews(
                        @PathVariable Long merchantId) {

                return ApiResponse.success(
                                "MERCHANT_REVIEW_LIST_SUCCESS",
                                "取得商家評論成功",
                                merchantService.getMerchantReviews(merchantId));
        }

}
