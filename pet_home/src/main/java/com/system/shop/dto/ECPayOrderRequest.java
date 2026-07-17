package com.system.shop.dto;


import lombok.Data;

@Data
public class ECPayOrderRequest {
    private Integer orderId;        // 你系統內部的訂單編號
    private Integer totalAmount; // 訂單總金額（整數，綠界不接受小數）
    private String itemName;     // 商品名稱，多項用 # 分隔
}