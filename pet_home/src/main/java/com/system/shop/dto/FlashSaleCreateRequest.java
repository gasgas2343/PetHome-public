package com.system.shop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FlashSaleCreateRequest {
    private Integer productSkuId;
    private BigDecimal salePrice;
    private Integer saleStock;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Getters and Setters
    public Integer getProductSkuId() { return productSkuId; }
    public void setProductSkuId(Integer productSkuId) { this.productSkuId = productSkuId; }
    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }
    public Integer getSaleStock() { return saleStock; }
    public void setSaleStock(Integer saleStock) { this.saleStock = saleStock; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
