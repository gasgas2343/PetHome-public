package com.system.shop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.system.shop.entity.Cart;

public class CartResponse {
    private Integer       id;
    private Long       userId;
    private Integer       productSkuId;
    private BigDecimal    unitPrice;
    private Integer       quantity;
    private BigDecimal    totalAmount;
    private LocalDateTime updatedAt;

    public static CartResponse from(Cart c) {
        CartResponse r = new CartResponse();
        r.id           = c.getId();
        r.userId       = c.getUserId();
        r.productSkuId = c.getProductSkuId();
        r.unitPrice    = c.getUnitPrice();
        r.quantity     = c.getQuantity();
        r.totalAmount  = c.getTotalAmount();
        r.updatedAt    = c.getUpdatedAt();
        return r;
    }

    public Integer getId() { return id; }
    public Long getUserId() { return userId; }
    public Integer getProductSkuId() { return productSkuId; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

