package com.system.shop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.system.shop.entity.Payment;

public class PaymentResponse {
    private Integer       id;
    private Integer       orderId;
    private String        provider;
    private String        providerTxId;
    private String        method;
    private BigDecimal    amount;
    private String        status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    public static PaymentResponse from(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.id           = p.getId();
        r.orderId      = p.getOrderId();
        r.provider     = p.getProvider();
        r.providerTxId = p.getProviderTxId();
        r.method       = p.getMethod();
        r.amount       = p.getAmount();
        r.status       = p.getStatus();
        r.paidAt       = p.getPaidAt();
        r.createdAt    = p.getCreatedAt();
        return r;
    }

    public Integer getId() { return id; }
    public Integer getOrderId() { return orderId; }
    public String getProvider() { return provider; }
    public String getProviderTxId() { return providerTxId; }
    public String getMethod() { return method; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
