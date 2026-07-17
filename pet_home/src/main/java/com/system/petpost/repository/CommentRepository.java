package com.system.petpost.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

import com.system.petpost.entity.ForumComment;

// @Repository
public interface CommentRepository
                extends JpaRepository<ForumComment, Long> {

        // 查詢某篇文章所有留言
        List<ForumComment> findByPostId(
                        Long postId);

        // 查詢某篇文章正常留言
        List<ForumComment> findByPostIdAndStatus(
                        Long postId,
                        Byte status);

        // 查詢某位會員所有留言
        List<ForumComment> findByUserId(
                        Long userId);

        // 查詢某留言底下的回覆
        List<ForumComment> findByParentCommentIdAndStatus(
                        Long parentCommentId,
                        Byte status);

        // 中文註解：後台查詢留言時，排除已刪除留言。
        List<ForumComment> findByStatusNotOrderByCreatedAtDesc(Byte status);

        // 中文註解：查詢某篇文章未刪除留言，包含正常與被隱藏留言。
        List<ForumComment> findByPostIdAndStatusNotOrderByCreatedAtAsc(
                        Long postId,
                        Byte status);

        // 中文註解：查詢某留言底下未刪除的回覆，包含正常與被隱藏回覆。
        List<ForumComment> findByParentCommentIdAndStatusNotOrderByCreatedAtAsc(
                        Long parentCommentId,
                        Byte status);

}