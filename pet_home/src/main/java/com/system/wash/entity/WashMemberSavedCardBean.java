package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashMemberSavedCard")
public class WashMemberSavedCardBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saved_card_id")
    private Integer savedCardId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "card_token", length = 255)
    private String cardToken;

    @Column(name = "masked_card_no", length = 20)
    private String maskedCardNo;

    @Column(name = "card_brand")
    private Byte cardBrand;

    @Column(name = "expire_year", length = 4, columnDefinition = "CHAR(4)")
    private String expireYear;

    @Column(name = "expire_month", length = 2, columnDefinition = "CHAR(2)")
    private String expireMonth;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getSavedCardId() { return savedCardId; } public void setSavedCardId(Integer savedCardId) { this.savedCardId = savedCardId; }
    public Long getMemberId() { return memberId; } public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getCardToken() { return cardToken; } public void setCardToken(String cardToken) { this.cardToken = cardToken; }
    public String getMaskedCardNo() { return maskedCardNo; } public void setMaskedCardNo(String maskedCardNo) { this.maskedCardNo = maskedCardNo; }
    public Byte getCardBrand() { return cardBrand; } public void setCardBrand(Byte cardBrand) { this.cardBrand = cardBrand; }
    public String getExpireYear() { return expireYear; } public void setExpireYear(String expireYear) { this.expireYear = expireYear; }
    public String getExpireMonth() { return expireMonth; } public void setExpireMonth(String expireMonth) { this.expireMonth = expireMonth; }
    public Boolean getIsDefault() { return isDefault; } public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
