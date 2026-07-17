package com.system.shop.service;

import com.system.shop.dto.OrderEmailDTO;
import com.system.shop.dto.OrderEmailItemDTO;
import com.system.shop.entity.*;
import com.system.shop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PaymentService {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private WebhookLogRepository webhookLogRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private ProductSkuRepository productSkuRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private EmailService emailService;

    // ── 你原本的 getPaymentsByOrderId / createPayment / handleCallback / getAllPayments 不動 ──

    public List<Payment> getPaymentsByOrderId(Integer orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Transactional
    public Payment createPayment(Integer orderId, String provider, String method, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setProvider(provider);
        payment.setMethod(method);
        payment.setAmount(amount);
        payment.setStatus("pending");
        return paymentRepository.save(payment);
    }

   @Transactional
public Payment handleCallback(String idempotencyKey, String providerTxId, String rawCallback, String provider, String eventType) {
    if (webhookLogRepository.existsByIdempotencyKey(idempotencyKey)) {
        return paymentRepository.findByProviderTxId(providerTxId).orElse(null);
    }

    Payment payment = paymentRepository.findByProviderTxId(providerTxId)
            .orElseThrow(() -> new RuntimeException("找不到對應的付款記錄: " + providerTxId));

    // 依 eventType 判斷是成功還是失敗，不再一律設成 paid
    boolean isSuccess = "PAYMENT_SUCCESS".equals(eventType);
    payment.setStatus(isSuccess ? "paid" : "failed");
    payment.setPaidAt(isSuccess ? LocalDateTime.now() : null);
    payment.setRawCallback(rawCallback);
    paymentRepository.save(payment);

    // 訂單狀態也對應調整，付款失敗不應該顯示 Paid
    orderRepository.findById(payment.getOrderId()).ifPresent(order -> {
        order.setPaymentStatus(isSuccess ? "Paid" : "Failed");
        orderRepository.save(order);
    });

    WebhookLog log = new WebhookLog();
    log.setProvider(provider);
    log.setEventType(eventType);
    log.setIdempotencyKey(idempotencyKey);
    log.setPayload(rawCallback);
    webhookLogRepository.save(log);

    // 只有付款成功才寄送訂單確認信，失敗不寄
    if (isSuccess) {
        orderRepository.findById(payment.getOrderId()).ifPresent(order ->
            sendOrderEmail(order, payment)
        );
    }

    return payment;
}

    @Transactional
    public Payment mockPay(Integer orderId, String method, BigDecimal amount) {
        // 1. 建立付款記錄
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setProvider("MOCK");
        payment.setMethod(method);
        payment.setAmount(amount);
        payment.setStatus("pending");
        payment.setProviderTxId("MOCK-" + orderId + "-" + System.currentTimeMillis());
        paymentRepository.save(payment);

        // 2. 模擬付款成功
        payment.setStatus("paid");
        payment.setPaidAt(LocalDateTime.now());
        payment.setRawCallback("{\"mock\":true}");
        paymentRepository.save(payment);

        // 3. 更新訂單付款狀態
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setPaymentStatus("Paid");
            orderRepository.save(order);
        });

        // 4. 寄出訂單確認信
        orderRepository.findById(orderId).ifPresent(order ->
            sendOrderEmail(order, payment)
        );

        return payment;
    }

    public List<Payment> getAllPayments(Integer orderId, String status) {
        if (orderId != null) return paymentRepository.findByOrderId(orderId);
        if (status != null && !status.isEmpty()) return paymentRepository.findByStatus(status);
        return paymentRepository.findAll();
    }

    // ── 寄信輔助方法 ──────────────────────────────────────────────

    private void sendOrderEmail(Order order, Payment payment) {
        // 取得會員 email（從 Order.recipientName 取姓名，email 存在 User，
        // 但你的 Order 只有 userId，需要自行加 UserRepository 查詢）
        // 若暫時先寫死測試用 email，可先這樣：
        String email = getUserEmail(order.getUserId());
        if (email == null) return;

        // 組商品明細
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderEmailItemDTO> emailItems = items.stream().map(item -> {
            ProductSku sku = productSkuRepository.findById(item.getProductSkuId()).orElse(null);
            Product product = (sku != null)
                    ? productRepository.findById(sku.getProductId()).orElse(null)
                    : null;

            String productName = (product != null) ? product.getName() : "（商品已下架）";
            String spec = Stream.of(
                    sku != null ? sku.getFlavor() : null,
                    sku != null ? sku.getSize()   : null
            ).filter(s -> s != null && !s.isBlank())
             .collect(Collectors.joining(" / "));

            return new OrderEmailItemDTO(productName, spec, item.getQuantity(), item.getBuyPrice());
        }).collect(Collectors.toList());

        // 組 DTO
        OrderEmailDTO dto = new OrderEmailDTO();
        dto.setCustomerEmail(email);
        dto.setCustomerName(order.getRecipientName());
        dto.setOrderNo(order.getOrderNo());
        dto.setPaymentNo(payment.getProviderTxId());
        dto.setShippingAddress(
            (order.getShippingCity() != null ? order.getShippingCity() : "") +
            (order.getShippingAddress() != null ? order.getShippingAddress() : "")
        );
        dto.setRecipientPhone(order.getRecipientPhone());
        dto.setItems(emailItems);
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalAmount(order.getFinalAmount());
        // 優惠券代碼：Order 沒存 code，暫時不傳（信件會自動隱藏折扣行）
        dto.setCouponCode(null);

        emailService.sendOrderConfirmationEmail(dto);
    }

    private String getUserEmail(Long userId) {
        // TODO: 注入 UserRepository 後改成真實查詢
        // return userRepository.findById(userId).map(User::getEmail).orElse(null);

        // 暫時測試用，先寫死你自己的信箱
        return "your_test_email@gmail.com";
    }
}