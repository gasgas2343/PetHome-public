package com.system.petpost.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "forum_appeals")
public class Appeal {

    // 申訴編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appeal_id")
    private Long appealId;

    // 關聯檢舉編號
    @Column(name = "report_id")
    private Long reportId;

    // 中文註解：申訴目標類型，例如 POST / COMMENT。
    @Column(name = "target_type")
    private String targetType;

    // 中文註解：申訴目標 ID，例如文章 ID。
    @Column(name = "target_id")
    private Long targetId;

    // 申訴人ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 申訴類型
    // POST = 文章申訴
    // COMMENT = 留言申訴
    @Column(name = "appeal_type", nullable = false)
    private String appealType;

    // 申訴主旨
    @Column(name = "subject", nullable = false)
    private String subject;

    // 申訴原因
    @Column(name = "reason", nullable = false)
    private String reason;

    // 證明圖片
    @Column(name = "image_url")
    private String imageUrl;

    // 處理狀態
    // 1 = 待審核
    // 2 = 成立 / 通過
    // 3 = 駁回
    @Column(name = "status")
    private Byte status = 1;

    // 審核管理員ID
    @Column(name = "reviewed_by")
    private Long reviewedBy;

    // 審核時間
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // 管理員回覆
    @Column(name = "admin_note")
    private String adminNote;

    // 建立時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
