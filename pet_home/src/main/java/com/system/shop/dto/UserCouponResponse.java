package com.system.shop.dto;

import java.time.LocalDateTime;

import com.system.shop.entity.UserCoupon;

public class UserCouponResponse {
    private Integer       id;
    private Long       userId;
    private Integer       couponId;
    private String        sourceType;
    private String        sourceKey;
    private Integer       isUsed;
    private LocalDateTime usedAt;
    private LocalDateTime issuedAt;
    private LocalDateTime expireAt;

    public static UserCouponResponse from(UserCoupon uc) {
        UserCouponResponse r = new UserCouponResponse();
        r.id         = uc.getId();
        r.userId     = uc.getUserId();
        r.couponId   = uc.getCouponId();
        r.sourceType = uc.getSourceType();
        r.sourceKey  = uc.getSourceKey();
        r.isUsed     = uc.getIsUsed();
        r.usedAt     = uc.getUsedAt();
        r.issuedAt   = uc.getIssuedAt();
        r.expireAt   = uc.getExpireAt();
        return r;
    }

    public Integer getId() { return id; }
    public Long getUserId() { return userId; }
    public Integer getCouponId() { return couponId; }
    public String getSourceType() { return sourceType; }
    public String getSourceKey() { return sourceKey; }
    public Integer getIsUsed() { return isUsed; }
    public LocalDateTime getUsedAt() { return usedAt; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public LocalDateTime getExpireAt() { return expireAt; }
}
