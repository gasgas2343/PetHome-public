package com.system.shop.dto;

import java.math.BigDecimal;

import com.system.shop.entity.OrderItem;

public class OrderItemResponse {
    private Integer    id;
    private Integer    orderId;
    private Integer    productSkuId;
    private Integer    quantity;
    private BigDecimal buyPrice;
    private BigDecimal subtotal;

    public static OrderItemResponse from(OrderItem i) {
        OrderItemResponse r = new OrderItemResponse();
        r.id           = i.getId();
        r.orderId      = i.getOrderId();
        r.productSkuId = i.getProductSkuId();
        r.quantity     = i.getQuantity();
        r.buyPrice     = i.getBuyPrice();
        r.subtotal     = i.getBuyPrice().multiply(BigDecimal.valueOf(i.getQuantity()));
        return r;
    }

    public Integer getId() { return id; }
    public Integer getOrderId() { return orderId; }
    public Integer getProductSkuId() { return productSkuId; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getBuyPrice() { return buyPrice; }
    public BigDecimal getSubtotal() { return subtotal; }
}
