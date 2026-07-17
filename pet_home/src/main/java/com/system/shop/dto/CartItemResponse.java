package com.system.shop.dto;

import java.math.BigDecimal;

public class CartItemResponse {
    private Integer cartId;
    private Integer productSkuId;
    private String productName;
    private String imageUrl;
    private BigDecimal unitPrice;
    private BigDecimal originalPrice;  // ← 新增
    private Integer quantity;
    private BigDecimal totalAmount;

    public CartItemResponse(Integer cartId, Integer productSkuId, String productName,
                       String imageUrl, BigDecimal unitPrice, BigDecimal originalPrice,
                       Integer quantity, BigDecimal totalAmount) {
        this.cartId = cartId;
        this.productSkuId = productSkuId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.originalPrice = originalPrice;  // ← 新增
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public Integer getCartId() { return cartId; }
    public Integer getProductSkuId() { return productSkuId; }
    public String getProductName() { return productName; }
    public String getImageUrl() { return imageUrl; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getOriginalPrice() { return originalPrice; }  // ← 新增
    public Integer getQuantity() { return quantity; }
    public BigDecimal getTotalAmount() { return totalAmount; }
}