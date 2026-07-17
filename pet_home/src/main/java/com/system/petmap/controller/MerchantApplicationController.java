package com.system.petmap.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.MerchantApplicationCreateRequest;
import com.system.petmap.dto.MerchantApplicationHandleRequest;
import com.system.petmap.entity.MerchantApplication;
import com.system.petmap.service.MerchantApplicationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map/merchant-applications")
@RequiredArgsConstructor
public class MerchantApplicationController {

        private final MerchantApplicationService merchantApplicationService;

        @RequirePermission("MERCHANT_APP_CREATE")
        @PostMapping
        public ApiResponse<MerchantApplication> create(
                        @Valid @RequestBody MerchantApplicationCreateRequest request) {

                return ApiResponse.success(
                                "MERCHANT_APPLICATION_CREATE_SUCCESS",
                                "商家申請送出成功",
                                merchantApplicationService.create(request));
        }

        @RequirePermission("MERCHANT_APP_USER_LIST")
        @GetMapping("/user/{userId}")
        public ApiResponse<List<MerchantApplication>> findByUserId(
                        @PathVariable Long userId) {

                return ApiResponse.success(
                                "MERCHANT_APPLICATION_LIST_SUCCESS",
                                "取得商家申請成功",
                                merchantApplicationService.findByUserId(userId));
        }

        @RequirePermission("MERCHANT_APP_PENDING_LIST")
        @GetMapping("/pending")
        public ApiResponse<List<MerchantApplication>> findPending() {

                return ApiResponse.success(
                                "MERCHANT_APPLICATION_PENDING_SUCCESS",
                                "取得待審核申請成功",
                                merchantApplicationService.findPending());
        }

        @RequirePermission("MERCHANT_APP_APPROVE")
        @PutMapping("/{applicationId}/approve")
        public ApiResponse<MerchantApplication> approve(
                        @PathVariable Integer applicationId,
                        @RequestBody MerchantApplicationHandleRequest request) {

                return ApiResponse.success(
                                "MERCHANT_APPLICATION_APPROVE_SUCCESS",
                                "商家申請審核通過",
                                merchantApplicationService.approve(
                                                applicationId,
                                                request));
        }

        @RequirePermission("MERCHANT_APP_REJECT")
        @PutMapping("/{applicationId}/reject")
        public ApiResponse<MerchantApplication> reject(
                        @PathVariable Integer applicationId,
                        @RequestBody MerchantApplicationHandleRequest request) {

                return ApiResponse.success(
                                "MERCHANT_APPLICATION_REJECT_SUCCESS",
                                "商家申請已駁回",
                                merchantApplicationService.reject(
                                                applicationId,
                                                request));
        }
}