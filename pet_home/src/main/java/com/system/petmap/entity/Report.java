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
@Entity(name = "PetMapReport")
@Table(name = "map_reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "reporter_id")
    private Long reporterId;

    @Column(name = "place_id")
    private Integer placeId;

    @Column(name = "review_id")
    private Integer reviewId;

    @Column(name = "reason")
    private String reason;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "admin_note")
    private String adminNote;

    @Column(name = "handled_by")
    private Long handledBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "handled_at")
    private LocalDateTime handledAt;
}
