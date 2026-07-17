package com.system.petpost.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.system.petpost.constant.NotificationType;
import com.system.petpost.entity.ForumComment;
import com.system.petpost.entity.ForumCommentLike;
import com.system.petpost.repository.CommentRepository;
import com.system.petpost.repository.ForumCommentLikeRepository;

import com.system.member.dto.request.CreateNotificationRequest;
import com.system.member.service.UserNotificationService;
import com.system.petpost.entity.ForumPost;
import com.system.petpost.repository.ForumPostRepository;

import lombok.RequiredArgsConstructor;

// 留言按讚服務
//
// 功能：
// 1. 留言按讚
// 2. 取消按讚
// 3. 查詢是否已按讚
// 4. 更新留言按讚數
@Service
@RequiredArgsConstructor
public class CommentLikeService {
        private final ForumPostRepository postRepository;

        private final UserNotificationService userNotificationService;

        private final ForumUserStatusService forumUserStatusService;

        private final NotificationService notificationService;

        // 留言資料庫
        private final CommentRepository commentRepository;

        // 留言按讚資料庫
        private final ForumCommentLikeRepository commentLikeRepository;

        // 留言按讚
        public void likeComment(
                        Long commentId,
                        Long userId) {

                forumUserStatusService.checkForumUsable(userId);

                ForumComment comment = commentRepository.findById(
                                commentId)
                                .orElseThrow(() -> new RuntimeException(
                                                "留言不存在"));

                // 防止重複按讚
                if (commentLikeRepository
                                .existsByCommentIdAndUserId(
                                                commentId,
                                                userId)) {

                        throw new RuntimeException(
                                        "已按讚過此留言");
                }

                ForumCommentLike like = new ForumCommentLike();

                like.setCommentId(
                                commentId);

                like.setUserId(
                                userId);

                like.setCreatedAt(
                                LocalDateTime.now());

                commentLikeRepository.save(
                                like);

                // 更新按讚數
                comment.setLikeCount(
                                (int) commentLikeRepository
                                                .countByCommentId(
                                                                commentId));

                commentRepository.save(
                                comment);

                ForumPost post = postRepository.findById(comment.getPostId())
                                .orElseThrow(() -> new RuntimeException("文章不存在"));

                if (!post.getUserId().equals(userId)) {
                        notificationService.createNotification(
                                        post.getUserId(),
                                        NotificationType.COMMENT_LIKE,
                                        post.getPostId(),
                                        "您的文章內有留言收到新的按讚");
                        userNotificationService.createNotification(
                                        CreateNotificationRequest.of(
                                                        post.getUserId(),
                                                        "FORUM",
                                                        "COMMENT_LIKE",
                                                        "您的文章收到新的留言按讚",
                                                        "您的文章內有留言收到按讚。",
                                                        "POST",
                                                        post.getPostId()));
                }
        }

        // 取消按讚
        public void unlikeComment(
                        Long commentId,
                        Long userId) {

                ForumComment comment = commentRepository.findById(
                                commentId)
                                .orElseThrow(() -> new RuntimeException(
                                                "留言不存在"));

                ForumCommentLike like = commentLikeRepository
                                .findByCommentIdAndUserId(
                                                commentId,
                                                userId)
                                .orElseThrow(() -> new RuntimeException(
                                                "尚未按讚"));

                commentLikeRepository.delete(
                                like);

                // 更新按讚數
                comment.setLikeCount(
                                (int) commentLikeRepository
                                                .countByCommentId(
                                                                commentId));

                commentRepository.save(
                                comment);
        }

        // 查詢是否已按讚
        public boolean isLiked(
                        Long commentId,
                        Long userId) {

                return commentLikeRepository
                                .existsByCommentIdAndUserId(
                                                commentId,
                                                userId);
        }

        // 查詢留言按讚數量
        public long countCommentLikes(
                        Long commentId) {

                return commentLikeRepository
                                .countByCommentId(
                                                commentId);
        }

}