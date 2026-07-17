package com.system.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.shop.dto.CouponResponse;
import com.system.shop.dto.Result;
import com.system.shop.entity.UserCoupon;
import com.system.shop.service.CouponService;
import com.system.member.security.annotation.RequirePermission;

import java.util.List;

@Tag(name = "顧客優惠券服務", description = "查看與領取優惠券")
@RestController
// @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/customer/coupons")
public class CustomerCouponController {

    @Autowired 
    private CouponService couponService;
    @RequirePermission("COUPON_READ_MY")
    @Operation(summary = "查詢我的優惠券")
@GetMapping("/my")
public Result<List<CouponResponse>> getMyCoupons(@RequestHeader("userId") Long userId) {
    
    // 1. 直接呼叫 Service，接住它已經處理好的 DTO 列表
    List<CouponResponse> couponResponses = couponService.getUserCoupons(userId);

    // 2. 直接包裝成 Result.success 回傳！
    return Result.success(couponResponses);
}

    @Operation(summary = "顧客領取優惠券")
    @RequirePermission("COUPON_CLAIM")
    @PostMapping("/claim/{couponId}")
    public Result<UserCoupon> claimCoupon(
            @RequestHeader("userId") Long userId, 
            @PathVariable Integer couponId) {
        
        // 領取時 sourceKey 可以用 "CLAIM_" + couponId + "_" + System.currentTimeMillis() 確保唯一性
        String sourceKey = "CLAIM_" + couponId + "_" + System.currentTimeMillis();
        
        UserCoupon claimedCoupon = couponService.issueCoupon(
                userId, 
                couponId, 
                "CUSTOMER_CLAIM", 
                sourceKey, 
                null // 使用 Coupon 預設的 validDays
        );
        return Result.success(claimedCoupon);
    }
}
