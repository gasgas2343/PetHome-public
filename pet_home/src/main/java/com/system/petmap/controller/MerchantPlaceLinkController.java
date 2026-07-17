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
import com.system.petmap.dto.MerchantPlaceLinkCreateRequest;
import com.system.petmap.entity.MerchantPlaceLink;
import com.system.petmap.service.MerchantPlaceLinkService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map/merchant-place-links")
@RequiredArgsConstructor
public class MerchantPlaceLinkController {

        private final MerchantPlaceLinkService merchantPlaceLinkService;

        @RequirePermission("MERCHANT_PLACE_LINK_CREATE")
        @PostMapping
        public ApiResponse<MerchantPlaceLink> create(
                        @Valid @RequestBody MerchantPlaceLinkCreateRequest request) {

                return ApiResponse.success(
                                "MERCHANT_PLACE_LINK_CREATE_SUCCESS",
                                "商家綁定景點成功",
                                merchantPlaceLinkService.create(request));
        }

        @RequirePermission("MERCHANT_PLACE_LINK_LIST")
        @GetMapping("/merchant/{merchantId}")
        public ApiResponse<List<MerchantPlaceLink>> findByMerchantId(
                        @PathVariable Long merchantId) {

                return ApiResponse.success(
                                "MERCHANT_PLACE_LINK_LIST_SUCCESS",
                                "取得商家綁定景點成功",
                                merchantPlaceLinkService.findByMerchantId(
                                                merchantId));
        }

        @RequirePermission("MERCHANT_PLACE_LINK_DELETE")
        @DeleteMapping("/{merchantId}/{placeId}")
        public ApiResponse<Void> delete(
                        @PathVariable Long merchantId,
                        @PathVariable Integer placeId) {

                merchantPlaceLinkService.delete(
                                merchantId,
                                placeId);

                return ApiResponse.success(
                                "MERCHANT_PLACE_LINK_DELETE_SUCCESS",
                                "解除商家景點綁定成功",
                                null);
        }
}
