package com.system.shop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.system.shop.entity.Coupon;

public class CouponResponse {
    private Integer       id;
    private String        title;
    private String        couponScope;
    private String        discountType;
    private BigDecimal    discountValue;
    private BigDecimal    minSpend;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer       validDays;
    private Integer       isActive;
    private Integer userCouponId; // ✅ 新增，對應 user_coupon 的 id
    

    public static CouponResponse from(Coupon c) {
        CouponResponse r = new CouponResponse();
        r.id            = c.getId();
        r.title         = c.getTitle();
        r.couponScope   = c.getCouponScope();
        r.discountType  = c.getDiscountType();
        r.discountValue = c.getDiscountValue();
        r.minSpend      = c.getMinSpend();
        r.startAt       = c.getStartAt();
        r.endAt         = c.getEndAt();
        r.validDays     = c.getValidDays();
        r.isActive      = c.getIsActive();
       
        return r;
    }
     public static CouponResponse fromWithUserCouponId(Coupon c, Integer userCouponId) {
    CouponResponse r = CouponResponse.from(c);
    r.userCouponId = userCouponId;
    return r;
}



    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getCouponScope() { return couponScope; }
    public String getDiscountType() { return discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public BigDecimal getMinSpend() { return minSpend; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public Integer getValidDays() { return validDays; }
    public Integer getIsActive() { return isActive; }
    public Integer getUserCouponId() { return userCouponId; }
}