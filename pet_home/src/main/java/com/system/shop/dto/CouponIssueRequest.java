package com.system.shop.dto;

public class CouponIssueRequest {
    private Long userId;
    private Integer couponId;
    private String  sourceType;  // e.g. REGISTER / ACTIVITY / MANUAL
    private String  sourceKey;   // 防重複發放的唯一鍵
    private Integer validDays;   // 覆寫預設有效天數（可為 null）

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
