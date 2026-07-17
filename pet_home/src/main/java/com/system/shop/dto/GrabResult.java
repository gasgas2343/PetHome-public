package com.system.shop.dto;

import java.math.BigDecimal;

public class GrabResult {
    private Integer flashSaleId;
    private Integer quantity;
    private BigDecimal salePrice;
    private BigDecimal subtotal;

    public GrabResult(Integer flashSaleId, Integer quantity, BigDecimal salePrice) {
        this.flashSaleId = flashSaleId;
        this.quantity = quantity;
        this.salePrice = salePrice;
        this.subtotal = salePrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    public Integer getFlashSaleId() { return flashSaleId; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getSalePrice() { return salePrice; }
    public BigDecimal getSubtotal() { return subtotal; }
}