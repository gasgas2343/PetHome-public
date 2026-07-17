package com.system.shop.dto;

import java.math.BigDecimal;

public class OrderEmailItemDTO {

    private String productName;   // Product.name
    private String spec;          // flavor + size 組合，例如「雞肉口味 / 1.5kg」
    private Integer quantity;     // OrderItem.quantity
    private BigDecimal buyPrice;  // OrderItem.buyPrice（購買時單價）
    private BigDecimal subtotal;  // buyPrice × quantity

    public OrderEmailItemDTO(String productName, String spec,
                              Integer quantity, BigDecimal buyPrice) {
        this.productName = productName;
        this.spec        = spec;
        this.quantity    = quantity;
        this.buyPrice    = buyPrice;
        this.subtotal    = buyPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getProductName() { return productName; }
    public String getSpec()        { return spec; }
    public Integer getQuantity()   { return quantity; }
    public BigDecimal getBuyPrice(){ return buyPrice; }
    public BigDecimal getSubtotal(){ return subtotal; }
}
