package com.system.shop.dto;

import java.math.BigDecimal;

public class FlashSaleDTO {
    private Integer flashSaleId;
    private Integer productId;
    private String  productName;
    private String  imageUrl;
    private BigDecimal originalPrice;  // SKU 原價
    private BigDecimal salePrice;      // 閃購特賣價
    private Integer saleStock;
    private String  startTime;
    private String  endTime;

    // Getters & Setters
    public Integer getFlashSaleId() { return flashSaleId; }
    public void setFlashSaleId(Integer flashSaleId) { this.flashSaleId = flashSaleId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public Integer getSaleStock() { return saleStock; }
    public void setSaleStock(Integer saleStock) { this.saleStock = saleStock; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}