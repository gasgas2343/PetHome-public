package com.system.shop.dto;

public class OrderCreateRequest {
    private Long userId;
    private Integer userCouponId;       // 可為 null（不使用優惠券）
    private String  recipientName;
    private String  recipientPhone;
    private String  shippingCity;
    private String  shippingAddress;
    private String  postalCode;
    private String  shippingMethod;     // e.g. HOME_DELIVERY / CVS
    private String  cvsStoreId;         // 超商取貨門市編號（非超商時可為 null）

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getUserCouponId() { return userCouponId; }
    public void setUserCouponId(Integer userCouponId) { this.userCouponId = userCouponId; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }
    public String getShippingCity() { return shippingCity; }
    public void setShippingCity(String shippingCity) { this.shippingCity = shippingCity; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public String getCvsStoreId() { return cvsStoreId; }
    public void setCvsStoreId(String cvsStoreId) { this.cvsStoreId = cvsStoreId; }
}
