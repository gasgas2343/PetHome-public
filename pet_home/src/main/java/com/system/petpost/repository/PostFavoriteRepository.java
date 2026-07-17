package com.system.petpost.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petpost.entity.ForumPostFavorite;

// 文章收藏資料庫
public interface PostFavoriteRepository
        extends JpaRepository<ForumPostFavorite, Long> {

    // 是否已收藏
    boolean existsByPostIdAndUserId(
            Long postId,
            Long userId);

    // 查詢收藏紀錄
    Optional<ForumPostFavorite>
    findByPostIdAndUserId(
            Long postId,
            Long userId);

    // 統計收藏數
    long countByPostId(
            Long postId);
}