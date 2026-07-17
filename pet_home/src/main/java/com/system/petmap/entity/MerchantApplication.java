package com.system.petmap.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "map_merchant_applications")
public class MerchantApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "tax_id")
    private String taxId;

    @Column(name = "business_license_url")
    private String businessLicenseUrl;

    @Column(name = "place_name")
    private String placeName;

    @Column(name = "place_address")
    private String placeAddress;

    @Column(name = "status")
    private String status;

    @Column(name = "admin_note")
    private String adminNote;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
