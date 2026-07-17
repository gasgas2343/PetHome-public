package com.system.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.shop.dto.OrderCreateRequest;
import com.system.shop.entity.*;
import com.system.shop.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired private CartRepository cartRepository;
    @Autowired private ProductSkuRepository productSkuRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private CouponService couponService;

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在: " + orderId));
    }

    public List<OrderItem> getOrderItems(Integer orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Transactional
    public Order createOrderFromCart(OrderCreateRequest req) {

        Long userId = req.getUserId();
        Integer userCouponId = req.getUserCouponId();

        List<Cart> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("購物車為空");
        }

        Order order = new Order();
        order.setOrderNo("ORD-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        order.setUserId(userId);
        order.setUserCouponId(userCouponId);
        order.setOrderStatus("Pending");
        order.setPaymentStatus("Unpaid");
        order.setShippingStatus("Preparing");
        order.setRecipientName(req.getRecipientName());
        order.setRecipientPhone(req.getRecipientPhone());
        order.setShippingCity(req.getShippingCity());
        order.setShippingAddress(req.getShippingAddress());
        order.setPostalCode(req.getPostalCode());
        order.setShippingMethod(req.getShippingMethod());
        order.setCvsStoreId(req.getCvsStoreId());

        // ✅ 第一次迴圈：扣庫存 + 用購物車單價計算總金額
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : cartItems) {
            ProductSku sku = productSkuRepository
                    .findByIdForUpdate(cart.getProductSkuId())
                    .orElseThrow(() -> new RuntimeException("商品不存在"));

            if (sku.getStock() < cart.getQuantity()) {
                throw new RuntimeException(sku.getSkuCode() + " 庫存不足");
            }

            sku.setStock(sku.getStock() - cart.getQuantity());
            productSkuRepository.save(sku);

            // ✅ 優先用購物車存的單價（含閃購價），沒有才用 SKU 原價
            BigDecimal unitPrice = cart.getUnitPrice() != null
                    ? cart.getUnitPrice()
                    : sku.getPrice();

            totalAmount = totalAmount.add(
                    unitPrice.multiply(BigDecimal.valueOf(cart.getQuantity()))
            );
        }

        // 優惠券折扣
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (userCouponId != null) {
            discountAmount = couponService.calculateDiscountAmount(userId, userCouponId, totalAmount);
            couponService.markAsUsed(userId, userCouponId);
        }

        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(totalAmount.subtract(discountAmount));

        Order savedOrder = orderRepository.save(order);

        // ✅ 第二次迴圈：儲存訂單明細，同樣用購物車單價
        for (Cart cart : cartItems) {
            ProductSku sku = productSkuRepository
                    .findById(cart.getProductSkuId())
                    .orElseThrow();

            BigDecimal unitPrice = cart.getUnitPrice() != null
                    ? cart.getUnitPrice()
                    : sku.getPrice();

            OrderItem item = new OrderItem();
            item.setOrderId(savedOrder.getId());
            item.setProductSkuId(cart.getProductSkuId());
            item.setQuantity(cart.getQuantity());
            item.setBuyPrice(unitPrice); // ✅ 閃購價正確記錄
            orderItemRepository.save(item);
        }

        cartRepository.deleteByUserId(userId);

        return savedOrder;
    }

    @Transactional
    public Order updateOrderStatus(Integer orderId, String orderStatus) {
        Order order = getOrderById(orderId);
        order.setOrderStatus(orderStatus);
        return orderRepository.save(order);
    }
    public List<Order> getAllOrders() {
    return orderRepository.findAll();
}

public List<Order> getOrdersByStatus(String status) {
    return orderRepository.findByOrderStatus(status);
}


}