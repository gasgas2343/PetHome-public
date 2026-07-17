package com.system.shop.dto;

import java.util.Map;

public class ReturnRequest {
    private Integer             orderId;
    private Long             userId;
    private String              reason;
    private String              refundMethod;   // e.g. ORIGINAL / BANK_TRANSFER
    /** key = orderItemId，value = 退貨數量 */
    private Map<Integer, Integer> items;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getRefundMethod() { return refundMethod; }
    public void setRefundMethod(String refundMethod) { this.refundMethod = refundMethod; }
    public Map<Integer, Integer> getItems() { return items; }
    public void setItems(Map<Integer, Integer> items) { this.items = items; }
}
