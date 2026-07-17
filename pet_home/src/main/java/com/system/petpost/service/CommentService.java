package com.system.petpost.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.petpost.constant.AiReviewConstant;
import com.system.petpost.constant.CommentStatus;
import com.system.petpost.constant.NotificationType;
import com.system.petpost.dto.CommentCreateDTO;
import com.system.petpost.dto.CommentUpdateDTO;
import com.system.petpost.dto.ReviewResultDTO;
import com.system.petpost.entity.ForumComment;
import com.system.petpost.entity.ForumPost;
import com.system.petpost.repository.CommentRepository;
import com.system.petpost.repository.ForumPostRepository;
import com.system.petpost.util.HotScoreUtil;

import com.system.member.dto.request.CreateNotificationRequest;
import com.system.member.service.UserNotificationService;

import lombok.RequiredArgsConstructor;

// 留言服務
@Service
@RequiredArgsConstructor
public class CommentService {

        private final UserNotificationService userNotificationService;

        // 文章檢舉狀態
        private final ForumUserStatusService forumUserStatusService;

        // 留言資料庫
        private final CommentRepository commentRepository;

        // AI審核服務
        private final AiReviewService aiReviewService;

        // 文章資料庫
        private final ForumPostRepository postRepository;

        // 通知服務
        private final NotificationService notificationService;

        // 新增留言
        public ForumComment create(
                        CommentCreateDTO dto,
                        Long userId) {

                forumUserStatusService.checkForumUsable(userId);

                // AI審核
                ReviewResultDTO review = aiReviewService.reviewContent(
                                dto.getContent());

                if (AiReviewConstant.BLOCKED.equals(
                                review.getAiResult())) {

                        throw new RuntimeException(
                                        "留言包含違規內容："
                                                        + review.getDetectedReason());
                }

                // 確認文章存在
                ForumPost post = postRepository.findById(
                                dto.getPostId())
                                .orElseThrow(() -> new RuntimeException(
                                                "文章不存在"));

                // 文章已鎖定禁止留言
                if (Boolean.TRUE.equals(
                                post.getIsLocked())) {

                        throw new RuntimeException(
                                        "文章已鎖定，禁止留言");
                }

                LocalDateTime now = LocalDateTime.now();

                ForumComment comment = new ForumComment();

                comment.setPostId(
                                dto.getPostId());

                comment.setUserId(
                                userId);

                comment.setParentCommentId(
                                dto.getParentCommentId());

                comment.setContent(
                                dto.getContent());

                comment.setLikeCount(0);

                // 正常狀態
                comment.setStatus(
                                CommentStatus.ACTIVE);

                comment.setCreatedAt(now);
                comment.setUpdatedAt(now);

                ForumComment savedComment = commentRepository.save(
                                comment);

                // 更新留言數
                post.setCommentCount(
                                post.getCommentCount() + 1);

                // 更新熱門分數
                post.setTrendingScore(
                                HotScoreUtil.calculate(
                                                post.getLikeCount(),
                                                post.getCommentCount(),
                                                post.getViewCount()));

                postRepository.save(post);

                // 通知文章作者
                if (!post.getUserId().equals(
                                userId)) {

                        notificationService.createNotification(
                                        post.getUserId(),
                                        NotificationType.POST_COMMENT,
                                        post.getPostId(),
                                        "您的文章有新留言");
                        userNotificationService.createNotification(
                                        CreateNotificationRequest.of(
                                                        post.getUserId(),
                                                        "FORUM",
                                                        "POST_COMMENT",
                                                        "您的文章有新留言",
                                                        "您的文章收到新的留言。",
                                                        "POST",
                                                        post.getPostId()));
                }

                return savedComment;
        }

        public List<ForumComment> findByPostId(
                        Long postId) {

                // 中文註解：
                // 前台文章留言區要同步顯示「被隱藏留言」。
                // 因此不能只查 ACTIVE。
                // status = 1 正常留言：顯示原內容。
                // status = 2 被隱藏留言：前端顯示「此留言已被隱藏」。
                // status = 3 已刪除留言：不顯示。
                return commentRepository
                                .findByPostIdAndStatusNotOrderByCreatedAtAsc(
                                                postId,
                                                CommentStatus.DELETED);
        }

        // 查詢單筆留言
        public ForumComment findById(
                        Long commentId) {

                if (commentId == null) {

                        throw new IllegalArgumentException(
                                        "留言ID不可為空");
                }

                ForumComment comment = commentRepository.findById(
                                commentId)
                                .orElseThrow(() -> new RuntimeException(
                                                "留言不存在"));

                // 已刪除視同不存在
                if (comment.getStatus() == CommentStatus.DELETED) {

                        throw new RuntimeException(
                                        "留言不存在");
                }

                return comment;
        }

        // 查詢留言回覆
        public List<ForumComment> findReplies(
                        Long commentId) {
                if (commentId == null) {

                        throw new IllegalArgumentException(
                                        "留言ID不可為空");
                }

                // 中文註解：
                // 樓中樓回覆也要同步顯示被隱藏狀態。
                // status = 1 正常回覆：顯示原內容。
                // status = 2 被隱藏回覆：前端顯示「此留言已被隱藏」。
                // status = 3 已刪除回覆：不顯示。
                return commentRepository
                                .findByParentCommentIdAndStatusNotOrderByCreatedAtAsc(
                                                commentId,
                                                CommentStatus.DELETED);
        }

        // 修改留言
        public ForumComment update(

                        Long commentId,
                        Long userId,
                        CommentUpdateDTO dto) {

                forumUserStatusService.checkForumUsable(userId);

                ForumComment comment = findById(commentId);

                // 已刪除不可修改
                if (comment.getStatus() == CommentStatus.DELETED) {

                        throw new IllegalStateException(
                                        "留言已刪除");
                }

                // 只能修改自己的留言
                if (!comment.getUserId().equals(
                                userId)) {

                        throw new RuntimeException(
                                        "只能修改自己的留言");
                }

                // AI審核
                ReviewResultDTO review = aiReviewService.reviewContent(
                                dto.getContent());

                if (AiReviewConstant.BLOCKED.equals(
                                review.getAiResult())) {

                        throw new RuntimeException(
                                        "留言包含違規內容："
                                                        + review.getDetectedReason());
                }

                comment.setContent(
                                dto.getContent());

                comment.setUpdatedAt(
                                LocalDateTime.now());

                return commentRepository.save(
                                comment);
        }

        // 軟刪除留言
        public void delete(
                        Long commentId,
                        Long userId) {

                ForumComment comment = findById(commentId);

                // 只能刪除自己的留言
                if (!comment.getUserId().equals(
                                userId)) {

                        throw new RuntimeException(
                                        "只能刪除自己的留言");
                }

                // 避免刪除已被封鎖的留言
                if (comment.getStatus() == CommentStatus.BLOCKED) {

                        throw new IllegalStateException(
                                        "留言已封鎖");
                }

                // 已刪除不可重複刪除
                if (comment.getStatus() == CommentStatus.DELETED) {

                        throw new IllegalStateException(
                                        "留言已刪除");
                }

                comment.setStatus(
                                CommentStatus.DELETED);

                comment.setUpdatedAt(
                                LocalDateTime.now());

                commentRepository.save(
                                comment);

                // 更新文章留言數
                ForumPost post = postRepository.findById(
                                comment.getPostId())
                                .orElseThrow(() -> new RuntimeException(
                                                "文章不存在"));

                if (post.getCommentCount() > 0) {

                        post.setCommentCount(
                                        post.getCommentCount() - 1);
                }

                // 更新熱門分數
                post.setTrendingScore(
                                HotScoreUtil.calculate(
                                                post.getLikeCount(),
                                                post.getCommentCount(),
                                                post.getViewCount()));

                postRepository.save(post);
        }
}