
package com.system.petpost.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "forum_notifications")
public class Notification {

    // 通知編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    // 接收會員ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 通知類型
    // COMMENT = 留言通知
    // REPORT = 檢舉通知
    // APPEAL = 申訴通知
    // SYSTEM = 系統通知
    @Column(name = "type", nullable = false)
    private String type;

    // 關聯資料ID
    // 例如：文章ID、留言ID、檢舉ID、申訴ID
    @Column(name = "reference_id")
    private Long referenceId;

    // 通知內容
    @Column(name = "message", nullable = false, length = 500)
    private String message;

    // 相同通知累積次數
    @Column(name = "notification_count")
    private Integer notificationCount = 1;

    // 是否已讀
    @Column(name = "is_read")
    private Boolean isRead = false;

    // 建立時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 更新時間
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    // 中文註解：通知發送者ID，例如誰按讚、誰留言
    @Column(name = "sender_id")
    private Long senderId;

    // 中文註解：參考資料類型，例如 POST、COMMENT、REPORT、APPEAL
    @Column(name = "reference_type")
    private String referenceType;

    // 中文註解：前端可直接跳轉的目標網址
    @Column(name = "target_url")
    private String targetUrl;

    // 中文註解：是否刪除，採用軟刪除，不是真的刪資料
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}