package com.system.petpost.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

// 文章收藏
//
// 功能：
// 1. 記錄會員收藏文章
// 2. 防止重複收藏
// 3. 提供取消收藏依據
@Data
@Entity
@Table(
        name = "forum_post_favorites",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "post_id",
                                "user_id"
                        })
        })
public class ForumPostFavorite {

    // 收藏編號
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

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