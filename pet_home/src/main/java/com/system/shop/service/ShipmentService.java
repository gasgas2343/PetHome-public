package com.system.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.shop.entity.Order;
import com.system.shop.entity.Shipment;
import com.system.shop.repository.OrderRepository;
import com.system.shop.repository.ShipmentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShipmentService {

    @Autowired private ShipmentRepository shipmentRepository;
    @Autowired private OrderRepository    orderRepository;

    public List<Shipment> getShipmentsByOrder(Integer orderId) {
        return shipmentRepository.findByOrderId(orderId);
    }

    public Shipment getShipmentById(Integer shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("出貨記錄不存在: " + shipmentId));
    }

    /**
     * 建立出貨記錄，並將訂單 shippingStatus 改為 Shipped
     */
    @Transactional
    public Shipment createShipment(Integer orderId, String carrier,
                                   String trackingNo, String shippingMethod,
                                   LocalDate estimatedArrival) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在: " + orderId));

        if (!"Paid".equals(order.getPaymentStatus())) {
            throw new RuntimeException("訂單尚未付款，無法出貨");
        }

        Shipment shipment = new Shipment();
        shipment.setOrderId(orderId);
        shipment.setCarrier(carrier);
        shipment.setTrackingNo(trackingNo);
        shipment.setShippingMethod(shippingMethod);
        shipment.setEstimatedArrival(estimatedArrival);
        shipment.setStatus("shipped");
        shipment.setShippedAt(LocalDateTime.now());

        Shipment saved = shipmentRepository.save(shipment);

        // 同步更新訂單出貨狀態
        order.setShippingStatus("Shipped");
        orderRepository.save(order);

        return saved;
    }

    /**
     * 接收物流商 Webhook，更新配送狀態
     */
    @Transactional
    public Shipment updateStatus(Integer shipmentId, String status, String rawCallback) {
        Shipment shipment = getShipmentById(shipmentId);
        shipment.setStatus(status);
        shipment.setRawCallback(rawCallback);

        if ("delivered".equals(status)) {
            shipment.setDeliveredAt(LocalDateTime.now());

            // 同步更新訂單狀態為已完成
            orderRepository.findById(shipment.getOrderId()).ifPresent(order -> {
                order.setShippingStatus("Delivered");
                order.setOrderStatus("Completed");
                orderRepository.save(order);
            });
        }

        return shipmentRepository.save(shipment);
    }
}

