package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashPayment")
public class WashPaymentBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @ManyToOne
    @JoinColumn(name = "appt_order_id")
    private WashAppointmentBean apptOrder;

    @Column(name = "member_point_id")
    private Integer memberPointId;

    @Column(name = "member_id")
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "saved_card_id")
    private WashMemberSavedCardBean savedCard;

    @Column(name = "pay_no", length = 20, unique = true, nullable = false)
    private String payNo;

    @Column(name = "payment_purpose")
    private Byte paymentPurpose;

    @Column(name = "transaction_type")
    private Byte transactionType;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "payment_method")
    private Byte paymentMethod;

    @Column(name = "point_topup_amount")
    private Integer pointTopupAmount;

    @Column(name = "point_topup_bonus")
    private Integer pointTopupBonus;

    @Column(name = "transaction_no", length = 100)
    private String transactionNo;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "payment_status")
    private Byte paymentStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and Setters
    public Integer getPaymentId() { return paymentId; } public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }
    public WashAppointmentBean getApptOrder() { return apptOrder; } public void setApptOrder(WashAppointmentBean apptOrder) { this.apptOrder = apptOrder; }
    public Integer getMemberPointId() { return memberPointId; } public void setMemberPointId(Integer memberPointId) { this.memberPointId = memberPointId; }
    public Long getMemberId() { return memberId; } public void setMemberId(Long memberId) { this.memberId = memberId; }
    public WashMemberSavedCardBean getSavedCard() { return savedCard; } public void setSavedCard(WashMemberSavedCardBean savedCard) { this.savedCard = savedCard; }
    public String getPayNo() { return payNo; } public void setPayNo(String payNo) { this.payNo = payNo; }
    public Byte getPaymentPurpose() { return paymentPurpose; } public void setPaymentPurpose(Byte paymentPurpose) { this.paymentPurpose = paymentPurpose; }
    public Byte getTransactionType() { return transactionType; } public void setTransactionType(Byte transactionType) { this.transactionType = transactionType; }
    public Integer getAmount() { return amount; } public void setAmount(Integer amount) { this.amount = amount; }
    public Byte getPaymentMethod() { return paymentMethod; } public void setPaymentMethod(Byte paymentMethod) { this.paymentMethod = paymentMethod; }
    public Integer getPointTopupAmount() { return pointTopupAmount; } public void setPointTopupAmount(Integer pointTopupAmount) { this.pointTopupAmount = pointTopupAmount; }
    public Integer getPointTopupBonus() { return pointTopupBonus; } public void setPointTopupBonus(Integer pointTopupBonus) { this.pointTopupBonus = pointTopupBonus; }
    public String getTransactionNo() { return transactionNo; } public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }
    public LocalDateTime getPaidAt() { return paidAt; } public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public Byte getPaymentStatus() { return paymentStatus; } public void setPaymentStatus(Byte paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
