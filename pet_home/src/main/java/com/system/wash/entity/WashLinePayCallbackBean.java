package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashLinePayCallback")
public class WashLinePayCallbackBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "callback_id")
    private Integer callbackId;

    @ManyToOne
    @JoinColumn(name = "linepay_txn_id")
    private WashLinePayTransactionBean linepayTxn;

    @Column(name = "order_id", length = 50)
    private String orderId;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "return_code", length = 10)
    private String returnCode;

    @Column(name = "return_message", length = 200)
    private String returnMessage;

    @Column(name = "pay_type", length = 50)
    private String payType;

    @Column(name = "masked_credit_card_number", length = 20)
    private String maskedCreditCardNumber;

    @Column(name = "raw_payload", length = 500)
    private String rawPayload;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "callback_type")
    private Byte callbackType;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    // Getters and Setters
    public Integer getCallbackId() { return callbackId; } public void setCallbackId(Integer callbackId) { this.callbackId = callbackId; }
    public WashLinePayTransactionBean getLinepayTxn() { return linepayTxn; } public void setLinepayTxn(WashLinePayTransactionBean linepayTxn) { this.linepayTxn = linepayTxn; }
    public String getOrderId() { return orderId; } public void setOrderId(String orderId) { this.orderId = orderId; }
    public Long getTransactionId() { return transactionId; } public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public String getReturnCode() { return returnCode; } public void setReturnCode(String returnCode) { this.returnCode = returnCode; }
    public String getReturnMessage() { return returnMessage; } public void setReturnMessage(String returnMessage) { this.returnMessage = returnMessage; }
    public String getPayType() { return payType; } public void setPayType(String payType) { this.payType = payType; }
    public String getMaskedCreditCardNumber() { return maskedCreditCardNumber; } public void setMaskedCreditCardNumber(String maskedCreditCardNumber) { this.maskedCreditCardNumber = maskedCreditCardNumber; }
    public String getRawPayload() { return rawPayload; } public void setRawPayload(String rawPayload) { this.rawPayload = rawPayload; }
    public Boolean getIsVerified() { return isVerified; } public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    public Byte getCallbackType() { return callbackType; } public void setCallbackType(Byte callbackType) { this.callbackType = callbackType; }
    public LocalDateTime getReceivedAt() { return receivedAt; } public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }
}
