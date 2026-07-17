package com.system.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.shop.dto.CouponIssueRequest;
import com.system.shop.dto.Result;
import com.system.shop.entity.UserCoupon;
import com.system.shop.service.CouponService;
import com.system.member.security.annotation.RequirePermission;

@Tag(name = "管理員優惠券管理", description = "優惠券發放與管理")
@RestController
// @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/admin/coupons")

public class AdminCouponController {

    @Autowired 
    private CouponService couponService;

//     @Operation(summary = "管理者手動發放優惠券給特定使用者")
//     @RequirePermission("COUPON_ADMIN_ISSUE")
//     @PostMapping("/issue")
//     public Result<UserCoupon> issueToUser(@RequestBody CouponIssueRequest req) {
//         // 使用者輸入的 userId, couponId, sourceType 與 sourceKey
//         UserCoupon issuedCoupon = couponService.issueCoupon(
//                 req.getUserId(), 
//                 req.getCouponId(), 
//                 req.getSourceType(), 
//                 req.getSourceKey(), 
//                 req.getValidDays()
//         );
//         return Result.success(issuedCoupon);
//     }
// }
@Operation(summary = "管理者手動發放優惠券給特定使用者")
@RequirePermission("COUPON_ADMIN_ISSUE")
@PostMapping("/issue")
public Result<UserCoupon> issueToUser(@RequestBody CouponIssueRequest req) {
    try {
        // 使用者輸入的 userId, couponId, sourceType 與 sourceKey
        UserCoupon issuedCoupon = couponService.issueCoupon(
                req.getUserId(), 
                req.getCouponId(), 
                req.getSourceType(), 
                req.getSourceKey(), 
                req.getValidDays()
        );
        return Result.success(issuedCoupon);
    } catch (RuntimeException e) {
        return Result.error(400, e.getMessage());
    }
}
}