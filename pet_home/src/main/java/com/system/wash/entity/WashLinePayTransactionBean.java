package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashLinePayTransaction")
public class WashLinePayTransactionBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "linepay_txn_id")
    private Integer linepayTxnId;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private WashPaymentBean payment;

    @Column(name = "order_id", length = 50, unique = true, nullable = false)
    private String orderId;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "product_name", length = 200, nullable = false)
    private String productName;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "currency", length = 3, nullable = false, columnDefinition = "CHAR(3)")
    private String currency = "TWD";

    @Column(name = "channel_id", length = 50, nullable = false)
    private String channelId;

    @Column(name = "nonce", length = 100, nullable = false)
    private String nonce;

    @Column(name = "signature", length = 500, nullable = false)
    private String signature;

    @Column(name = "request_uri", length = 255, nullable = false)
    private String requestUri;

    @Column(name = "payment_type")
    private Byte paymentType;

    @Column(name = "trade_status", nullable = false)
    private Byte tradeStatus = 0;

    @Column(name = "payment_url", length = 500)
    private String paymentUrl;

    @Column(name = "confirm_url", length = 255)
    private String confirmUrl;

    @Column(name = "reg_key", length = 200)
    private String regKey;

    @Column(name = "trade_date")
    private LocalDateTime tradeDate;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters omitted for brevity but standard implementation
    public Integer getLinepayTxnId() { return linepayTxnId; } public void setLinepayTxnId(Integer linepayTxnId) { this.linepayTxnId = linepayTxnId; }
    public WashPaymentBean getPayment() { return payment; } public void setPayment(WashPaymentBean payment) { this.payment = payment; }
    public String getOrderId() { return orderId; } public void setOrderId(String orderId) { this.orderId = orderId; }
    public Long getTransactionId() { return transactionId; } public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public String getProductName() { return productName; } public void setProductName(String productName) { this.productName = productName; }
    public Integer getAmount() { return amount; } public void setAmount(Integer amount) { this.amount = amount; }
    public String getCurrency() { return currency; } public void setCurrency(String currency) { this.currency = currency; }
    public String getChannelId() { return channelId; } public void setChannelId(String channelId) { this.channelId = channelId; }
    public String getNonce() { return nonce; } public void setNonce(String nonce) { this.nonce = nonce; }
    public String getSignature() { return signature; } public void setSignature(String signature) { this.signature = signature; }
    public String getRequestUri() { return requestUri; } public void setRequestUri(String requestUri) { this.requestUri = requestUri; }
    public Byte getPaymentType() { return paymentType; } public void setPaymentType(Byte paymentType) { this.paymentType = paymentType; }
    public Byte getTradeStatus() { return tradeStatus; } public void setTradeStatus(Byte tradeStatus) { this.tradeStatus = tradeStatus; }
    public String getPaymentUrl() { return paymentUrl; } public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    public String getConfirmUrl() { return confirmUrl; } public void setConfirmUrl(String confirmUrl) { this.confirmUrl = confirmUrl; }
    public String getRegKey() { return regKey; } public void setRegKey(String regKey) { this.regKey = regKey; }
    public LocalDateTime getTradeDate() { return tradeDate; } public void setTradeDate(LocalDateTime tradeDate) { this.tradeDate = tradeDate; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; } public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
