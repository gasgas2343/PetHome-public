package com.system.petpost.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "forum_reports")
public class Report {

    // 檢舉編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    // 檢舉人ID
    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    // 被檢舉人ID
    @Column(name = "reported_user_id", nullable = false)
    private Long reportedUserId;

    // 被檢舉文章ID
    @Column(name = "post_id")
    private Long postId;

    // 被檢舉留言ID
    @Column(name = "comment_id")
    private Long commentId;

    // 檢舉原因
    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

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

    // 管理員備註
    @Column(name = "admin_note", length = 500)
    private String adminNote;

    // 建立時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}