package com.system.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.shop.service.ReturnService;

import java.util.Map;

@Tag(name = "退貨退款", description = "申請退貨、審核、退款")
@RestController
@RequestMapping("/returns")
public class ReturnController {

    @Autowired private ReturnService returnService;

    @Operation(summary = "查詢用戶退貨記錄")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReturns(
            @Parameter(description = "用戶 ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(returnService.getReturnsByUser(userId));
    }

    @Operation(summary = "申請退貨", description = "items 的 key 為 orderItemId，value 為退貨數量")
    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody ReturnRequest req) {
        try {
            return ResponseEntity.ok(returnService.applyReturn(
                    req.getOrderId(), req.getUserId(),
                    req.getReason(), req.getRefundMethod(), req.getItems()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "審核通過退貨申請")
    @PatchMapping("/{returnId}/approve")
    public ResponseEntity<?> approve(
            @Parameter(description = "退貨單 ID", example = "1") @PathVariable Integer returnId) {
        try {
            return ResponseEntity.ok(returnService.approveReturn(returnId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "完成退款", description = "退款後 returnStatus 改為 Refunded，並記錄退款時間")
    @PatchMapping("/{returnId}/refund")
    public ResponseEntity<?> refund(
            @Parameter(description = "退貨單 ID", example = "1") @PathVariable Integer returnId) {
        try {
            return ResponseEntity.ok(returnService.completeRefund(returnId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


class ReturnRequest {
    private Integer orderId;
    private Long userId;
    private String reason;
    private String refundMethod;
    /** key = orderItemId, value = 退貨數量 */
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