package com.system.shop.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.system.shop.entity.Shipment;

public class ShipmentResponse {
    private Integer       id;
    private Integer       orderId;
    private String        carrier;
    private String        trackingNo;
    private String        shippingMethod;
    private String        status;
    private LocalDate     estimatedArrival;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;

    public static ShipmentResponse from(Shipment s) {
        ShipmentResponse r = new ShipmentResponse();
        r.id               = s.getId();
        r.orderId          = s.getOrderId();
        r.carrier          = s.getCarrier();
        r.trackingNo       = s.getTrackingNo();
        r.shippingMethod   = s.getShippingMethod();
        r.status           = s.getStatus();
        r.estimatedArrival = s.getEstimatedArrival();
        r.shippedAt        = s.getShippedAt();
        r.deliveredAt      = s.getDeliveredAt();
        r.createdAt        = s.getCreatedAt();
        return r;
    }

    public Integer getId() { return id; }
    public Integer getOrderId() { return orderId; }
    public String getCarrier() { return carrier; }
    public String getTrackingNo() { return trackingNo; }
    public String getShippingMethod() { return shippingMethod; }
    public String getStatus() { return status; }
    public LocalDate getEstimatedArrival() { return estimatedArrival; }
    public LocalDateTime getShippedAt() { return shippedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

