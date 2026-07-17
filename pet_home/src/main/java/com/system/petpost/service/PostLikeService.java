package com.system.petpost.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.system.petpost.constant.NotificationType;
import com.system.petpost.entity.ForumPost;
import com.system.petpost.entity.ForumPostLike;
import com.system.petpost.repository.ForumPostLikeRepository;
import com.system.petpost.repository.ForumPostRepository;
import com.system.petpost.util.HotScoreUtil;
import com.system.member.dto.request.CreateNotificationRequest;
import com.system.member.service.UserNotificationService;
import com.system.member.repository.UserNotificationRepository;

import lombok.RequiredArgsConstructor;

// 文章按讚服務
//
// 功能：
// 1. 文章按讚
// 2. 取消按讚
// 3. 更新按讚數
// 4. 更新熱門分數
@Service
@RequiredArgsConstructor
public class PostLikeService {
        private final UserNotificationRepository userNotificationRepository;

        private final UserNotificationService userNotificationService;

        private final ForumUserStatusService forumUserStatusService;

        private final NotificationService notificationService;

        // 文章資料庫
        private final ForumPostRepository postRepository;

        // 文章按讚資料庫
        private final ForumPostLikeRepository postLikeRepository;

        // 文章按讚
        public void likePost(
                        Long postId,
                        Long userId) {

                forumUserStatusService.checkForumUsable(userId);

                ForumPost post = postRepository.findById(postId)
                                .orElseThrow(() -> new RuntimeException(
                                                "文章不存在"));

                // 防止重複按讚
                if (postLikeRepository
                                .existsByPostIdAndUserId(
                                                postId,
                                                userId)) {

                        throw new RuntimeException(
                                        "已按讚過此文章");
                }

                ForumPostLike like = new ForumPostLike();

                like.setPostId(postId);

                like.setUserId(userId);

                like.setCreatedAt(
                                LocalDateTime.now());

                postLikeRepository.save(
                                like);

                // 更新按讚數
                post.setLikeCount(
                                (int) postLikeRepository
                                                .countByPostId(
                                                                postId));

                // 更新熱門分數
                post.setTrendingScore(
                                HotScoreUtil.calculate(
                                                post.getLikeCount(),
                                                post.getCommentCount(),
                                                post.getViewCount()));

                postRepository.save(
                                post);

                if (!post.getUserId().equals(userId)) {
                        notificationService.createNotification(
                                        post.getUserId(),
                                        NotificationType.POST_LIKE,
                                        post.getPostId(),
                                        "您的文章收到新的按讚");

                        boolean exists = userNotificationRepository
                                        .existsByUser_IdAndNotificationTypeAndIsReadFalse(
                                                        post.getUserId(),
                                                        "POST_LIKE");

                        if (!exists) {
                                userNotificationService.createNotification(
                                                CreateNotificationRequest.of(
                                                                post.getUserId(),
                                                                "FORUM",
                                                                "POST_LIKE",
                                                                "您的文章收到按讚",
                                                                "您的文章收到新的按讚。",
                                                                "POST",
                                                                post.getPostId()));
                        }
                }
        }

        
        // 取消按讚
        public void unlikePost(
                        Long postId,
                        Long userId) {

                ForumPost post = postRepository.findById(postId)
                                .orElseThrow(() -> new RuntimeException(
                                                "文章不存在"));

                ForumPostLike like = postLikeRepository
                                .findByPostIdAndUserId(
                                                postId,
                                                userId)
                                .orElseThrow(() -> new RuntimeException(
                                                "尚未按讚"));

                postLikeRepository.delete(
                                like);

                // 更新按讚數
                post.setLikeCount(
                                (int) postLikeRepository
                                                .countByPostId(
                                                                postId));

                // 更新熱門分數
                post.setTrendingScore(
                                HotScoreUtil.calculate(
                                                post.getLikeCount(),
                                                post.getCommentCount(),
                                                post.getViewCount()));

                postRepository.save(
                                post);
        }

        // 是否已按讚
        public boolean isLiked(
                        Long postId,
                        Long userId) {

                return postLikeRepository
                                .existsByPostIdAndUserId(
                                                postId,
                                                userId);
        }

        // 查詢文章按讚數量
        public long countPostLikes(
                        Long postId) {

                return postLikeRepository
                                .countByPostId(
                                                postId);
        }

}