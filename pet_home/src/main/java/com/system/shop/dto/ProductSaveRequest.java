package com.system.shop.dto;

import java.math.BigDecimal;
import java.util.List;

import com.system.shop.entity.ProductSku;

public class ProductSaveRequest {
    // 商品主資訊欄位
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Integer brandId;
    private Integer categoryId;
    private String imageUrl;
    private Integer status;

    // 接收前端自訂的 SKU 規格陣列
    private List<ProductSku> skus;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public List<ProductSku> getSkus() { return skus; }
    public void setSkus(List<ProductSku> skus) { this.skus = skus; }
}
