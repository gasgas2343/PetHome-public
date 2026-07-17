package com.system.petpost.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petpost.entity.ForumCommentLike;

// 留言按讚資料庫
public interface ForumCommentLikeRepository
        extends JpaRepository<ForumCommentLike, Long> {

    // 是否已按讚
    boolean existsByCommentIdAndUserId(
            Long commentId,
            Long userId);

    // 查詢按讚紀錄
    Optional<ForumCommentLike>
    findByCommentIdAndUserId(
            Long commentId,
            Long userId);

    // 統計留言按讚數
    long countByCommentId(
            Long commentId);
}