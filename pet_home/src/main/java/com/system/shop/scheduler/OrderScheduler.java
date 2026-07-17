package com.system.shop.scheduler;

import com.system.shop.entity.Order;
import com.system.shop.entity.OrderItem;
import com.system.shop.repository.OrderRepository;
import com.system.shop.repository.OrderItemRepository;
import com.system.shop.repository.ProductSkuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderScheduler {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private ProductSkuRepository productSkuRepository;

    @Scheduled(fixedRate = 60000) // 每分鐘檢查一次
    @Transactional
    public void cancelExpiredOrders() {
        // 找出 30 分鐘前建立、還是 Unpaid 的訂單
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(30);
        List<Order> expiredOrders = orderRepository
            .findByPaymentStatusAndCreatedAtBefore("Unpaid", expiredTime);

        for (Order order : expiredOrders) {
            // 1. 還原庫存
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            for (OrderItem item : items) {
                productSkuRepository.findById(item.getProductSkuId()).ifPresent(sku -> {
                    sku.setStock(sku.getStock() + item.getQuantity());
                    productSkuRepository.save(sku);
                });
            }

            // 2. 更新訂單狀態
            order.setOrderStatus("Cancelled");
            order.setPaymentStatus("Cancelled");
            orderRepository.save(order);

            System.out.println("訂單已自動取消：" + order.getOrderNo());
        }
    }
}