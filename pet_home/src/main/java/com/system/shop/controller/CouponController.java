package com.system.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.shop.dto.CouponResponse;
import com.system.shop.dto.Result;
import com.system.shop.entity.UserCoupon;
import com.system.shop.service.CouponService;

import java.util.List;
import com.system.member.security.annotation.RequirePermission;

@Tag(name = "優惠券", description = "查詢可用券、發放優惠券")
@RestController
@RequestMapping("/coupons")
// @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CouponController {

    @Autowired private CouponService couponService;
    @Autowired private com.system.shop.repository.CouponRepository couponRepository;

    @Operation(summary = "查詢用戶可用優惠券", description = "只回傳尚未使用（isUsed=0）的優惠券")
   @GetMapping("/user/{userId}")
@RequirePermission("COUPON_READ_USER")
public Result<List<CouponResponse>> getUserCoupons(
        @Parameter(description = "用戶 ID", example = "1") @PathVariable Long userId) {
    return Result.success(couponService.getUserCoupons(userId));
}

    @PostMapping("/issue")
    @RequirePermission("COUPON_ISSUE_GENERAL")
public Result<UserCoupon> issue(@RequestBody IssueCouponRequest req) {
    try {
        return Result.success(couponService.issueCoupon(
                req.getUserId(), req.getCouponId(),
                req.getSourceType(), req.getSourceKey(), req.getValidDays()));
    } catch (RuntimeException e) {
        return Result.error(400, e.getMessage());
    }
}
   @ GetMapping
   @RequirePermission("COUPON_READ_ALL")
public Result<List<com.system.shop.entity.Coupon>> getAllCoupons() {
    return Result.success(couponRepository.findAll());
}

@PostMapping
@RequirePermission("COUPON_CREATE")
public Result<com.system.shop.entity.Coupon> createCoupon(@RequestBody com.system.shop.entity.Coupon coupon) {
    return Result.success(couponRepository.save(coupon));
}

@DeleteMapping("/{id}")
@RequirePermission("COUPON_DELETE")
public Result<String> deleteCoupon(@PathVariable Integer id) {
    couponRepository.deleteById(id);
    return Result.success("刪除成功");
}



}

/**
 * 建議：若未來還有其他 API 使用，請將此類別移動至 com.system.member.shop.dto 套件下
 */
class IssueCouponRequest {
    private Long userId;
    private Integer couponId;
    private String sourceType;
    private String sourceKey;
    private Integer validDays;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getCouponId() { return couponId; }
    public void setCouponId(Integer couponId) { this.couponId = couponId; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourceKey() { return sourceKey; }
    public void setSourceKey(String sourceKey) { this.sourceKey = sourceKey; }
    public Integer getValidDays() { return validDays; }
    public void setValidDays(Integer validDays) { this.validDays = validDays; }
}