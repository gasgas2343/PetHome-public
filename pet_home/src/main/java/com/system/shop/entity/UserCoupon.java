package com.system.shop.entity;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop_user_coupons")
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_id", nullable = false)
    private Integer couponId;

    /** e.g. REGISTER / ACTIVITY / MANUAL */
    @Column(name = "source_type", nullable = false, length = 30)
    private String sourceType;

    /** 防止重複發放的唯一鍵值 */
    @Column(name = "source_key", nullable = false, length = 50)
    private String sourceKey;

    @Column(name = "is_used")
    private Integer isUsed = 0;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @PrePersist
    protected void onCreate() {
        if (issuedAt == null) issuedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getCouponId() { return couponId; }
    public void setCouponId(Integer couponId) { this.couponId = couponId; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getSourceKey() { return sourceKey; }
    public void setSourceKey(String sourceKey) { this.sourceKey = sourceKey; }

    public Integer getIsUsed() { return isUsed; }
    public void setIsUsed(Integer isUsed) { this.isUsed = isUsed; }

    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getExpireAt() { return expireAt; }
    public void setExpireAt(LocalDateTime expireAt) { this.expireAt = expireAt; }
}
