package com.system.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.shop.dto.Result;
import com.system.shop.entity.Shipment;
import com.system.shop.service.ShipmentService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "出貨物流", description = "建立出貨記錄、追蹤配送狀態")
@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    @Autowired private ShipmentService shipmentService;

    @Operation(summary = "查詢訂單的出貨記錄")
    @GetMapping("/order/{orderId}")
    public Result<List<Shipment>> getByOrder(
            @Parameter(description = "訂單 ID", example = "1") @PathVariable Integer orderId) {
        return Result.success(shipmentService.getShipmentsByOrder(orderId));
    }

    @Operation(summary = "查詢單筆出貨記錄")
    @GetMapping("/{shipmentId}")
    public Result<Shipment> getById(
            @Parameter(description = "出貨記錄 ID", example = "1") @PathVariable Integer shipmentId) {
        return Result.success(shipmentService.getShipmentById(shipmentId));
    }

    @Operation(summary = "建立出貨記錄", description = "訂單須已付款，建立後訂單狀態自動更新")
    @PostMapping("/create")
    public Result<Shipment> create(@RequestBody ShipmentCreateRequest req) {
        return Result.success(shipmentService.createShipment(
                req.getOrderId(), req.getCarrier(), req.getTrackingNo(),
                req.getShippingMethod(), req.getEstimatedArrival()));
    }

    @Operation(summary = "更新配送狀態（物流 Webhook）")
    @PatchMapping("/{shipmentId}/status")
    public Result<Shipment> updateStatus(
            @PathVariable Integer shipmentId,
            @RequestBody ShipmentStatusRequest req) {
        return Result.success(shipmentService.updateStatus(
                shipmentId, req.getStatus(), req.getRawCallback()));
    }
}

// 建議移至 com.system.member.shop.dto 套件
class ShipmentCreateRequest {
    private Integer orderId;
    private String carrier;
    private String trackingNo;
    private String shippingMethod;
    private LocalDate estimatedArrival;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public LocalDate getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(LocalDate estimatedArrival) { this.estimatedArrival = estimatedArrival; }
}

class ShipmentStatusRequest {
    private String status;
    private String rawCallback;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRawCallback() { return rawCallback; }
    public void setRawCallback(String rawCallback) { this.rawCallback = rawCallback; }
}