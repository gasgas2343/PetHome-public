package com.system.petpost.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petpost.entity.ForumPostLike;

public interface ForumPostLikeRepository
        extends JpaRepository<ForumPostLike, Long> {

    // 是否已按讚
    boolean existsByPostIdAndUserId(
            Long postId,
            Long userId);

    // 查詢按讚紀錄
    Optional<ForumPostLike>
    findByPostIdAndUserId(
            Long postId,
            Long userId);

    // 統計文章按讚數
    long countByPostId(
            Long postId);
}