package com.system.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.shop.entity.OrderReturn;
import com.system.shop.entity.ReturnItem;
import com.system.shop.repository.OrderItemRepository;
import com.system.shop.repository.OrderReturnRepository;
import com.system.shop.repository.ReturnItemRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReturnService {

    @Autowired private OrderReturnRepository orderReturnRepository;
    @Autowired private ReturnItemRepository returnItemRepository;
    @Autowired private OrderItemRepository orderItemRepository;

    public List<OrderReturn> getReturnsByUser(Long userId) {
        return orderReturnRepository.findByUserId(userId);
    }

    /**
     * 申請退貨
     * @param items Map<orderItemId, returnQuantity>
     */
    @Transactional
    public OrderReturn applyReturn(Integer orderId, Long userId, String reason,
                                   String refundMethod, Map<Integer, Integer> items) {
        OrderReturn orderReturn = new OrderReturn();
        orderReturn.setReturnNo("RET-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        orderReturn.setOrderId(orderId);
        orderReturn.setUserId(userId);
        orderReturn.setReason(reason);
        orderReturn.setRefundMethod(refundMethod);
        orderReturn.setReturnStatus("Applied");
        OrderReturn saved = orderReturnRepository.save(orderReturn);

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
            Integer orderItemId = entry.getKey();
            Integer qty = entry.getValue();

            var orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new RuntimeException("訂單項目不存在: " + orderItemId));

            BigDecimal refundAmount = orderItem.getBuyPrice().multiply(BigDecimal.valueOf(qty));
            total = total.add(refundAmount);

            ReturnItem ri = new ReturnItem();
            ri.setReturnId(saved.getId());
            ri.setOrderItemId(orderItemId);
            ri.setQuantity(qty);
            ri.setRefundAmount(refundAmount);
            returnItemRepository.save(ri);
        }

        saved.setTotalRefundAmount(total);
        return orderReturnRepository.save(saved);
    }

    @Transactional
    public OrderReturn approveReturn(Integer returnId) {
        OrderReturn orderReturn = orderReturnRepository.findById(returnId)
                .orElseThrow(() -> new RuntimeException("退貨申請不存在"));
        orderReturn.setReturnStatus("Approved");
        return orderReturnRepository.save(orderReturn);
    }

    @Transactional
    public OrderReturn completeRefund(Integer returnId) {
        OrderReturn orderReturn = orderReturnRepository.findById(returnId)
                .orElseThrow(() -> new RuntimeException("退貨申請不存在"));
        orderReturn.setReturnStatus("Refunded");
        orderReturn.setRefundedAt(LocalDateTime.now());
        return orderReturnRepository.save(orderReturn);
    }
}