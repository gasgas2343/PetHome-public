package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "WashService")
public class WashServiceBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "service_name", length = 100)
    private String serviceName;

    @Column(name = "full_price")
    private Integer fullPrice;

    @Column(name = "onsale_price")
    private Integer onsalePrice;

    @Column(name = "promotion_price")
    private Integer promotionPrice;

    @Column(name = "promo_start")
    private LocalDateTime promoStart;

    @Column(name = "promo_end")
    private LocalDateTime promoEnd;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "member_point")
    private Integer memberPoint;

    // Getters and Setters
    public Integer getServiceId() { return serviceId; } public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }
    public String getServiceName() { return serviceName; } public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public Integer getFullPrice() { return fullPrice; } public void setFullPrice(Integer fullPrice) { this.fullPrice = fullPrice; }
    public Integer getOnsalePrice() { return onsalePrice; } public void setOnsalePrice(Integer onsalePrice) { this.onsalePrice = onsalePrice; }
    public Integer getPromotionPrice() { return promotionPrice; } public void setPromotionPrice(Integer promotionPrice) { this.promotionPrice = promotionPrice; }
    public LocalDateTime getPromoStart() { return promoStart; } public void setPromoStart(LocalDateTime promoStart) { this.promoStart = promoStart; }
    public LocalDateTime getPromoEnd() { return promoEnd; } public void setPromoEnd(LocalDateTime promoEnd) { this.promoEnd = promoEnd; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Integer getMemberPoint() { return memberPoint; } public void setMemberPoint(Integer memberPoint) { this.memberPoint = memberPoint; }
}
