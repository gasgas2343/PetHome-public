package com.system.shop.dto;

import java.math.BigDecimal;

import com.system.shop.entity.ProductSku;

public class ProductSkuResponse {
    private Integer    id;
    private Integer    productId;
    private String     skuCode;
    private String     flavor;
    private String     size;
    private BigDecimal price;
    private Integer    stock;
    private Integer    reservedStock;
    /** 實際可購買數量 = stock - reservedStock */
    private Integer    availableStock;

    public static ProductSkuResponse from(ProductSku s) {
        ProductSkuResponse r = new ProductSkuResponse();
        r.id             = s.getId();
        r.productId      = s.getProductId();
        r.skuCode        = s.getSkuCode();
        r.flavor         = s.getFlavor();
        r.size           = s.getSize();
        r.price          = s.getPrice();
        r.stock          = s.getStock();
        r.reservedStock  = s.getReservedStock();
        r.availableStock = s.getStock() - s.getReservedStock();
        return r;
    }

    public Integer getId() { return id; }
    public Integer getProductId() { return productId; }
    public String getSkuCode() { return skuCode; }
    public String getFlavor() { return flavor; }
    public String getSize() { return size; }
    public BigDecimal getPrice() { return price; }
    public Integer getStock() { return stock; }
    public Integer getReservedStock() { return reservedStock; }
    public Integer getAvailableStock() { return availableStock; }
}
