package com.system.petpost.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "forum_comments")
public class ForumComment {

    // 留言編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    // 文章編號
    @Column(name = "post_id", nullable = false)
    private Long postId;

    // 會員編號
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 父留言編號
    // null = 第一層留言
    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    // 留言內容
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    // 按讚數
    @Column(name = "like_count")
    private Integer likeCount = 0;

    // 狀態
    // 1 = 正常
    // 2 = 封鎖
    // 3 = 刪除
    @Column(name = "status")
    private Byte status = 1;

    // 封鎖原因
    @Column(name = "blocked_reason")
    private String blockedReason;

    // 封鎖時間
    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

    // 封鎖管理員ID
    @Column(name = "blocked_by")
    private Long blockedBy;


    // 建立時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 更新時間
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}