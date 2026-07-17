package com.system.shop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.system.shop.entity.Order;

public class OrderResponse {
    private Integer       id;
    private String        orderNo;
    private Long       userId;
    private Integer       userCouponId;
    private BigDecimal    totalAmount;
    private BigDecimal    discountAmount;
    private BigDecimal    finalAmount;
    private String        orderStatus;
    private String        paymentStatus;
    private String        shippingStatus;
    private String        recipientName;
    private String        recipientPhone;
    private String        shippingCity;
    private String        shippingAddress;
    private String        postalCode;
    private String        shippingMethod;
    private String        cvsStoreId;
    private LocalDateTime createdAt;

    public static OrderResponse from(Order o) {
        OrderResponse r = new OrderResponse();
        r.id              = o.getId();
        r.orderNo         = o.getOrderNo();
        r.userId          = o.getUserId();
        r.userCouponId    = o.getUserCouponId();
        r.totalAmount     = o.getTotalAmount();
        r.discountAmount  = o.getDiscountAmount();
        r.finalAmount     = o.getFinalAmount();
        r.orderStatus     = o.getOrderStatus();
        r.paymentStatus   = o.getPaymentStatus();
        r.shippingStatus  = o.getShippingStatus();
        r.recipientName   = o.getRecipientName();
        r.recipientPhone  = o.getRecipientPhone();
        r.shippingCity    = o.getShippingCity();
        r.shippingAddress = o.getShippingAddress();
        r.postalCode      = o.getPostalCode();
        r.shippingMethod  = o.getShippingMethod();
        r.cvsStoreId      = o.getCvsStoreId();
        r.createdAt       = o.getCreatedAt();
        return r;
    }

    public Integer getId() { return id; }
    public String getOrderNo() { return orderNo; }
    public Long getUserId() { return userId; }
    public Integer getUserCouponId() { return userCouponId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public String getOrderStatus() { return orderStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getShippingStatus() { return shippingStatus; }
    public String getRecipientName() { return recipientName; }
    public String getRecipientPhone() { return recipientPhone; }
    public String getShippingCity() { return shippingCity; }
    public String getShippingAddress() { return shippingAddress; }
    public String getPostalCode() { return postalCode; }
    public String getShippingMethod() { return shippingMethod; }
    public String getCvsStoreId() { return cvsStoreId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
