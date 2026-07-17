package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashLinePayRefund")
public class WashLinePayRefundBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private Integer refundId;

    @ManyToOne
    @JoinColumn(name = "linepay_txn_id")
    private WashLinePayTransactionBean linepayTxn;

    @Column(name = "refund_no", length = 30, unique = true, nullable = false)
    private String refundNo;

    @Column(name = "refund_amount", nullable = false)
    private Integer refundAmount;

    @Column(name = "nonce", length = 100, nullable = false)
    private String nonce;

    @Column(name = "signature", length = 500, nullable = false)
    private String signature;

    @Column(name = "request_uri", length = 255, nullable = false)
    private String requestUri;

    @Column(name = "refund_status", nullable = false)
    private Byte refundStatus = 0;

    @Column(name = "return_code", length = 10)
    private String returnCode;

    @Column(name = "return_message", length = 200)
    private String returnMessage;

    @Column(name = "refund_transaction_id")
    private Long refundTransactionId;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Getters and Setters
    public Integer getRefundId() { return refundId; } public void setRefundId(Integer refundId) { this.refundId = refundId; }
    public WashLinePayTransactionBean getLinepayTxn() { return linepayTxn; } public void setLinepayTxn(WashLinePayTransactionBean linepayTxn) { this.linepayTxn = linepayTxn; }
    public String getRefundNo() { return refundNo; } public void setRefundNo(String refundNo) { this.refundNo = refundNo; }
    public Integer getRefundAmount() { return refundAmount; } public void setRefundAmount(Integer refundAmount) { this.refundAmount = refundAmount; }
    public String getNonce() { return nonce; } public void setNonce(String nonce) { this.nonce = nonce; }
    public String getSignature() { return signature; } public void setSignature(String signature) { this.signature = signature; }
    public String getRequestUri() { return requestUri; } public void setRequestUri(String requestUri) { this.requestUri = requestUri; }
    public Byte getRefundStatus() { return refundStatus; } public void setRefundStatus(Byte refundStatus) { this.refundStatus = refundStatus; }
    public String getReturnCode() { return returnCode; } public void setReturnCode(String returnCode) { this.returnCode = returnCode; }
    public String getReturnMessage() { return returnMessage; } public void setReturnMessage(String returnMessage) { this.returnMessage = returnMessage; }
    public Long getRefundTransactionId() { return refundTransactionId; } public void setRefundTransactionId(Long refundTransactionId) { this.refundTransactionId = refundTransactionId; }
    public LocalDateTime getRequestedAt() { return requestedAt; } public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; } public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
