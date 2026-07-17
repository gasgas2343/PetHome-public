package com.system.petpost.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

// 留言按讚
//
// 功能：
// 1. 記錄會員對留言按讚
// 2. 防止重複按讚
// 3. 提供取消按讚依據
@Data
@Entity
@Table(
        name = "forum_comment_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "comment_id",
                                "user_id"
                        })
        })
public class ForumCommentLike {

    // 按讚編號
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "comment_like_id")
    private Long likeId;

    // 留言編號
    @Column(
            name = "comment_id",
            nullable = false)
    private Long commentId;

    // 會員編號
    @Column(
            name = "user_id",
            nullable = false)
    private Long userId;

    // 建立時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}