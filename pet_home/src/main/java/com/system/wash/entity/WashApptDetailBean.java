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
@Table(name = "WashApptDetail")
public class WashApptDetailBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apptdetail_id")
    private Integer apptdetailId;

    @ManyToOne
    @JoinColumn(name = "appt_order_id")
    private WashAppointmentBean apptOrder;

    @ManyToOne
    @JoinColumn(name = "package_service_item_id")
    private WashPackageServiceItemBean packageServiceItem;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private WashServiceBean service;

    @Column(name = "consumed_points")
    private Integer consumedPoints;

    @Column(name = "unit_price")
    private Integer unitPrice;

    @Column(name = "item_amount")
    private Integer itemAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Override
    public String toString() {
        return "WashApptDetailBean [apptdetailId=" + apptdetailId + ", apptOrder=" + apptOrder + ", unitPrice="
                + unitPrice + ", itemAmount=" + itemAmount + "]";
    }

    // Getters and Setters
    public Integer getApptdetailId() { return apptdetailId; }
    public void setApptdetailId(Integer apptdetailId) { this.apptdetailId = apptdetailId; }

    public WashAppointmentBean getApptOrder() { return apptOrder; }
    public void setApptOrder(WashAppointmentBean apptOrder) { this.apptOrder = apptOrder; }

    public WashPackageServiceItemBean getPackageServiceItem() { return packageServiceItem; }
    public void setPackageServiceItem(WashPackageServiceItemBean packageServiceItem) { this.packageServiceItem = packageServiceItem; }

    public WashServiceBean getService() { return service; }
    public void setService(WashServiceBean service) { this.service = service; }

    public Integer getConsumedPoints() { return consumedPoints; }
    public void setConsumedPoints(Integer consumedPoints) { this.consumedPoints = consumedPoints; }

    public Integer getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Integer unitPrice) { this.unitPrice = unitPrice; }

    public Integer getItemAmount() { return itemAmount; }
    public void setItemAmount(Integer itemAmount) { this.itemAmount = itemAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
