package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashDepositPolicy")
public class WashDepositPolicyBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Integer policyId;

    @Column(name = "deposit_amount")
    private Integer depositAmount;

    @Column(name = "free_cancel_days")
    private Byte freeCancelDays;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and Setters
    public Integer getPolicyId() { return policyId; } public void setPolicyId(Integer policyId) { this.policyId = policyId; }
    public Integer getDepositAmount() { return depositAmount; } public void setDepositAmount(Integer depositAmount) { this.depositAmount = depositAmount; }
    public Byte getFreeCancelDays() { return freeCancelDays; } public void setFreeCancelDays(Byte freeCancelDays) { this.freeCancelDays = freeCancelDays; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getEffectiveFrom() { return effectiveFrom; } public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
