package com.system.shop.dto;

import java.math.BigDecimal;

public class PaymentCreateRequest {
    private Integer    orderId;
    private String     provider;   // e.g. ECPAY / LINEPAY / NEWEBPAY
    private String     method;     // e.g. CREDIT / ATM / CVS
    private BigDecimal amount;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
