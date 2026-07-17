package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashPackageServiceItem")
public class WashPackageServiceItemBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_service_item_id")
    private Integer packageServiceItemId;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private WashServiceBean service;

    @Column(name = "type_code", length = 50)
    private String typeCode;

    @Column(name = "type_name", length = 100)
    private String typeName;

    @Column(name = "period_minutes")
    private Short periodMinutes;

    @Column(name = "quantity", nullable = false)
    private Short quantity = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Integer getPackageServiceItemId() { return packageServiceItemId; } public void setPackageServiceItemId(Integer packageServiceItemId) { this.packageServiceItemId = packageServiceItemId; }
    public WashServiceBean getService() { return service; } public void setService(WashServiceBean service) { this.service = service; }
    public String getTypeCode() { return typeCode; } public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public String getTypeName() { return typeName; } public void setTypeName(String typeName) { this.typeName = typeName; }
    public Short getPeriodMinutes() { return periodMinutes; } public void setPeriodMinutes(Short periodMinutes) { this.periodMinutes = periodMinutes; }
    public Short getQuantity() { return quantity; } public void setQuantity(Short quantity) { this.quantity = quantity; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
