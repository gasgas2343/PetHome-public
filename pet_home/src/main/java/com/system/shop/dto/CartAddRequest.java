package com.system.shop.dto;

public class CartAddRequest {
    private Long userId;
    private Integer productSkuId;
    private Integer quantity = 1;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getProductSkuId() { return productSkuId; }
    public void setProductSkuId(Integer productSkuId) { this.productSkuId = productSkuId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
