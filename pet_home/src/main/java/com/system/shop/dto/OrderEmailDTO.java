package com.system.shop.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderEmailDTO {

    private String customerName;       // 收件人姓名（Order.recipientName）
    private String customerEmail;      // 寄信目標（從 User 取得）
    private String orderNo;            // Order.orderNo
    private String paymentNo;          // Payment.paymentNo
    private String shippingAddress;    // 完整地址：city + address
    private String recipientPhone;     // Order.recipientPhone

    private List<OrderEmailItemDTO> items; // 商品明細

    private BigDecimal totalAmount;    // 商品小計（Order.totalAmount）
    private BigDecimal discountAmount; // 折扣金額（Order.discountAmount），null 或 0 則隱藏
    private String couponCode;         // 優惠券代碼（可為 null）
    private BigDecimal finalAmount;    // 實付金額（Order.finalAmount）

    // ---- Getters & Setters ----

    public String getCustomerName()    { return customerName; }
    public void setCustomerName(String v) { this.customerName = v; }

    public String getCustomerEmail()   { return customerEmail; }
    public void setCustomerEmail(String v) { this.customerEmail = v; }

    public String getOrderNo()         { return orderNo; }
    public void setOrderNo(String v)   { this.orderNo = v; }

    public String getPaymentNo()       { return paymentNo; }
    public void setPaymentNo(String v) { this.paymentNo = v; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String v) { this.shippingAddress = v; }

    public String getRecipientPhone()  { return recipientPhone; }
    public void setRecipientPhone(String v) { this.recipientPhone = v; }

    public List<OrderEmailItemDTO> getItems() { return items; }
    public void setItems(List<OrderEmailItemDTO> v) { this.items = v; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal v) { this.totalAmount = v; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal v) { this.discountAmount = v; }

    public String getCouponCode()      { return couponCode; }
    public void setCouponCode(String v){ this.couponCode = v; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal v) { this.finalAmount = v; }

    /** Thymeleaf 判斷是否顯示折扣列 */
    public boolean isHasDiscount() {
        return discountAmount != null
            && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }
}
