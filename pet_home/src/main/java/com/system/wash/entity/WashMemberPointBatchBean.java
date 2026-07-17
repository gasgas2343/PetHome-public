package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashMemberPointBatch")
public class WashMemberPointBatchBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Integer batchId;

    @Column(name = "member_id")
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private WashPaymentBean payment;

    @ManyToOne
    @JoinColumn(name = "appt_order_id")
    private WashAppointmentBean apptOrder;

    @Column(name = "batch_no", length = 30, unique = true, nullable = false)
    private String batchNo;

    @Column(name = "source_type")
    private Byte sourceType;

    @Column(name = "issued_points")
    private Integer issuedPoints;

    @Column(name = "remaining_points")
    private Integer remainingPoints;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "is_expired", nullable = false)
    private Boolean isExpired = false;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getBatchId() { return batchId; } public void setBatchId(Integer batchId) { this.batchId = batchId; }
    public Long getMemberId() { return memberId; } public void setMemberId(Long memberId) { this.memberId = memberId; }
    public WashPaymentBean getPayment() { return payment; } public void setPayment(WashPaymentBean payment) { this.payment = payment; }
    public WashAppointmentBean getApptOrder() { return apptOrder; } public void setApptOrder(WashAppointmentBean apptOrder) { this.apptOrder = apptOrder; }
    public String getBatchNo() { return batchNo; } public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public Byte getSourceType() { return sourceType; } public void setSourceType(Byte sourceType) { this.sourceType = sourceType; }
    public Integer getIssuedPoints() { return issuedPoints; } public void setIssuedPoints(Integer issuedPoints) { this.issuedPoints = issuedPoints; }
    public Integer getRemainingPoints() { return remainingPoints; } public void setRemainingPoints(Integer remainingPoints) { this.remainingPoints = remainingPoints; }
    public LocalDateTime getIssuedAt() { return issuedAt; } public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    public LocalDateTime getExpiredAt() { return expiredAt; } public void setExpiredAt(LocalDateTime expiredAt) { this.expiredAt = expiredAt; }
    public Boolean getIsExpired() { return isExpired; } public void setIsExpired(Boolean isExpired) { this.isExpired = isExpired; }
    public String getNote() { return note; } public void setNote(String note) { this.note = note; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
