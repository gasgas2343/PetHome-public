package com.system.wash.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "WashAppointment")
public class WashAppointmentBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appt_order_id")
    private Integer apptOrderId;

    @Column(name = "pet_id")
    private Long petId;

    @ManyToOne
    @JoinColumn(name = "pet_groomer_id")
    private WashPetGroomerBean petGroomer;

    @ManyToOne
    @JoinColumn(name = "line_notify_id")
    private WashLineNotifyBean lineNotify;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "appt_no", length = 20, unique = true, nullable = false)
    private String apptNo;

    @Column(name = "source_type")
    private Byte sourceType;

    @Column(name = "appt_status")
    private Byte apptStatus;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    @Column(name = "canceled_by", length = 100)
    private String canceledBy;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "appt_date")
    private LocalDateTime apptDate;

    @Column(name = "appt_start_time")
    private LocalDateTime apptStartTime;

    @Column(name = "deposit_amount", nullable = false)
    private Integer depositAmount = 200;

    @Column(name = "deposit_status", nullable = false)
    private Byte depositStatus = 0;

    @Column(name = "deposit_deadline")
    private LocalDateTime depositDeadline;

    @Column(name = "subtotal_amount")
    private Integer subtotalAmount;

    @Column(name = "discount_amount")
    private Integer discountAmount;

    @Column(name = "total_amount")
    private Integer totalAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Override
    public String toString() {
        return "WashAppointmentBean [apptOrderId=" + apptOrderId + ", apptNo=" + apptNo + ", apptStatus=" + apptStatus + "]";
    }

    // Getters and Setters
    public Integer getApptOrderId() { return apptOrderId; }
    public void setApptOrderId(Integer apptOrderId) { this.apptOrderId = apptOrderId; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public WashPetGroomerBean getPetGroomer() { return petGroomer; }
    public void setPetGroomer(WashPetGroomerBean petGroomer) { this.petGroomer = petGroomer; }

    public WashLineNotifyBean getLineNotify() { return lineNotify; }
    public void setLineNotify(WashLineNotifyBean lineNotify) { this.lineNotify = lineNotify; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public String getApptNo() { return apptNo; }
    public void setApptNo(String apptNo) { this.apptNo = apptNo; }

    public Byte getSourceType() { return sourceType; }
    public void setSourceType(Byte sourceType) { this.sourceType = sourceType; }

    public Byte getApptStatus() { return apptStatus; }
    public void setApptStatus(Byte apptStatus) { this.apptStatus = apptStatus; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public String getCanceledBy() { return canceledBy; }
    public void setCanceledBy(String canceledBy) { this.canceledBy = canceledBy; }

    public LocalDateTime getCanceledAt() { return canceledAt; }
    public void setCanceledAt(LocalDateTime canceledAt) { this.canceledAt = canceledAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getApptDate() { return apptDate; }
    public void setApptDate(LocalDateTime apptDate) { this.apptDate = apptDate; }

    public LocalDateTime getApptStartTime() { return apptStartTime; }
    public void setApptStartTime(LocalDateTime apptStartTime) { this.apptStartTime = apptStartTime; }

    public Integer getDepositAmount() { return depositAmount; }
    public void setDepositAmount(Integer depositAmount) { this.depositAmount = depositAmount; }

    public Byte getDepositStatus() { return depositStatus; }
    public void setDepositStatus(Byte depositStatus) { this.depositStatus = depositStatus; }

    public LocalDateTime getDepositDeadline() { return depositDeadline; }
    public void setDepositDeadline(LocalDateTime depositDeadline) { this.depositDeadline = depositDeadline; }

    public Integer getSubtotalAmount() { return subtotalAmount; }
    public void setSubtotalAmount(Integer subtotalAmount) { this.subtotalAmount = subtotalAmount; }

    public Integer getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Integer discountAmount) { this.discountAmount = discountAmount; }

    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
