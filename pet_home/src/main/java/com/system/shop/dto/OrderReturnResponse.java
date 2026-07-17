package com.system.shop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.system.shop.entity.OrderReturn;

public class OrderReturnResponse {
    private Integer       id;
    private String        returnNo;
    private Integer       orderId;
    private Long       userId;
    private String        reason;
    private String        returnStatus;
    private String        refundMethod;
    private BigDecimal    totalRefundAmount;
    private LocalDateTime refundedAt;
    private LocalDateTime createdAt;

    public static OrderReturnResponse from(OrderReturn ret) {
        OrderReturnResponse r = new OrderReturnResponse();
        r.id                = ret.getId();
        r.returnNo          = ret.getReturnNo();
        r.orderId           = ret.getOrderId();
        r.userId            = ret.getUserId();
        r.reason            = ret.getReason();
        r.returnStatus      = ret.getReturnStatus();
        r.refundMethod      = ret.getRefundMethod();
        r.totalRefundAmount = ret.getTotalRefundAmount();
        r.refundedAt        = ret.getRefundedAt();
        r.createdAt         = ret.getCreatedAt();
        return r;
    }

    public Integer getId() { return id; }
    public String getReturnNo() { return returnNo; }
    public Integer getOrderId() { return orderId; }
    public Long getUserId() { return userId; }
    public String getReason() { return reason; }
    public String getReturnStatus() { return returnStatus; }
    public String getRefundMethod() { return refundMethod; }
    public BigDecimal getTotalRefundAmount() { return totalRefundAmount; }
    public LocalDateTime getRefundedAt() { return refundedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}