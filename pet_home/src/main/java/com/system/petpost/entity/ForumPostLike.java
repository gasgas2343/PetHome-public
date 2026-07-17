package com.system.petpost.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

// 文章按讚
//
// 功能：
// 1. 記錄會員按讚文章
// 2. 防止重複按讚
// 3. 提供取消按讚依據
@Data
@Entity
@Table(
        name = "forum_post_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "post_id",
                                "user_id"
                        })
        })
public class ForumPostLike {

    // 按讚編號
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_id")
    private Long likeId;

    // 文章編號
    @Column(
            name = "post_id",
            nullable = false)
    private Long postId;

    // 會員編號
    @Column(
            name = "user_id",
            nullable = false)
    private Long userId;

    // 建立時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}