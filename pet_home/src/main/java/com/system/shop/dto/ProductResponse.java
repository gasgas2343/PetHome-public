package com.system.shop.dto;

import java.math.BigDecimal;

import com.system.shop.entity.Product;

public class ProductResponse {
    private Integer    id;
    private Integer    brandId;
    private String     name;
    private String     description;
    private BigDecimal basePrice;
    private Integer    categoryId; // 修正：改為 Integer
    private String imageUrl; // 新增此欄位

    public static ProductResponse from(Product p) {
        ProductResponse r = new ProductResponse();
        r.id          = p.getId();
        r.brandId     = p.getBrandId();
        r.name        = p.getName();
        r.description = p.getDescription();
        r.basePrice   = p.getBasePrice();
        r.categoryId  = p.getCategoryId(); // 修正：對應 Entity 的新欄位
        r.imageUrl = p.getImageUrl(); // 新增此行
        
        return r;
    }
    
    // 請補上對應的 Getters (用於序列化為 JSON)
    public Integer getId() { return id; }
    public Integer getBrandId() { return brandId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getBasePrice() { return basePrice; }
    public Integer getCategoryId() { return categoryId; }
    public String getImageUrl() { return imageUrl; } // 補上 Getter
}

