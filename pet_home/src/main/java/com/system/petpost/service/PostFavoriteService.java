package com.system.petpost.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.system.petpost.entity.ForumPost;
import com.system.petpost.entity.ForumPostFavorite;
import com.system.petpost.repository.ForumPostRepository;
import com.system.petpost.repository.PostFavoriteRepository;

import lombok.RequiredArgsConstructor;

// 文章收藏服務
//
// 功能：
// 1. 收藏文章
// 2. 取消收藏
// 3. 查詢是否已收藏
// 4. 更新收藏數
@Service
@RequiredArgsConstructor
public class PostFavoriteService {

        private final ForumUserStatusService forumUserStatusService;

        // 文章資料庫
        private final ForumPostRepository postRepository;

        // 收藏資料庫
        private final PostFavoriteRepository favoriteRepository;

        // 收藏文章
        public void favoritePost(
                        Long postId,
                        Long userId) {
                forumUserStatusService.checkForumUsable(userId);
                ForumPost post = postRepository.findById(
                                postId)
                                .orElseThrow(() -> new RuntimeException(
                                                "文章不存在"));

                // 防止重複收藏
                if (favoriteRepository
                                .existsByPostIdAndUserId(
                                                postId,
                                                userId)) {

                        throw new RuntimeException(
                                        "已收藏過此文章");
                }

                ForumPostFavorite favorite = new ForumPostFavorite();

                favorite.setPostId(
                                postId);

                favorite.setUserId(
                                userId);

                favorite.setCreatedAt(
                                LocalDateTime.now());

                favoriteRepository.save(
                                favorite);

                // 更新收藏數
                post.setFavoriteCount(
                                (int) favoriteRepository
                                                .countByPostId(
                                                                postId));

                postRepository.save(
                                post);
        }

        // 取消收藏
        public void unfavoritePost(
                        Long postId,
                        Long userId) {

                ForumPost post = postRepository.findById(
                                postId)
                                .orElseThrow(() -> new RuntimeException(
                                                "文章不存在"));

                ForumPostFavorite favorite = favoriteRepository
                                .findByPostIdAndUserId(
                                                postId,
                                                userId)
                                .orElseThrow(() -> new RuntimeException(
                                                "尚未收藏"));

                favoriteRepository.delete(
                                favorite);

                // 更新收藏數
                post.setFavoriteCount(
                                (int) favoriteRepository
                                                .countByPostId(
                                                                postId));

                postRepository.save(
                                post);
        }

        // 查詢是否已收藏
        public boolean isFavorited(
                        Long postId,
                        Long userId) {

                return favoriteRepository
                                .existsByPostIdAndUserId(
                                                postId,
                                                userId);
        }
}