package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashMemberPoint")
public class WashMemberPointBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_point_id")
    private Integer memberPointId;

    @Column(name = "member_id")
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private WashMemberPointBatchBean batch;

    @ManyToOne
    @JoinColumn(name = "appt_order_id")
    private WashAppointmentBean apptOrder;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private WashPaymentBean payment;

    @Column(name = "transaction_type")
    private Byte transactionType;

    @Column(name = "point_change")
    private Integer pointChange;

    @Column(name = "balance_after")
    private Integer balanceAfter;

    @Column(name = "source_type")
    private Byte sourceType;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "is_expired", nullable = false)
    private Boolean isExpired = false;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and Setters
    public Integer getMemberPointId() { return memberPointId; } public void setMemberPointId(Integer memberPointId) { this.memberPointId = memberPointId; }
    public Long getMemberId() { return memberId; } public void setMemberId(Long memberId) { this.memberId = memberId; }
    public WashMemberPointBatchBean getBatch() { return batch; } public void setBatch(WashMemberPointBatchBean batch) { this.batch = batch; }
    public WashAppointmentBean getApptOrder() { return apptOrder; } public void setApptOrder(WashAppointmentBean apptOrder) { this.apptOrder = apptOrder; }
    public WashPaymentBean getPayment() { return payment; } public void setPayment(WashPaymentBean payment) { this.payment = payment; }
    public Byte getTransactionType() { return transactionType; } public void setTransactionType(Byte transactionType) { this.transactionType = transactionType; }
    public Integer getPointChange() { return pointChange; } public void setPointChange(Integer pointChange) { this.pointChange = pointChange; }
    public Integer getBalanceAfter() { return balanceAfter; } public void setBalanceAfter(Integer balanceAfter) { this.balanceAfter = balanceAfter; }
    public Byte getSourceType() { return sourceType; } public void setSourceType(Byte sourceType) { this.sourceType = sourceType; }
    public LocalDateTime getExpiredAt() { return expiredAt; } public void setExpiredAt(LocalDateTime expiredAt) { this.expiredAt = expiredAt; }
    public Boolean getIsExpired() { return isExpired; } public void setIsExpired(Boolean isExpired) { this.isExpired = isExpired; }
    public String getNote() { return note; } public void setNote(String note) { this.note = note; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
