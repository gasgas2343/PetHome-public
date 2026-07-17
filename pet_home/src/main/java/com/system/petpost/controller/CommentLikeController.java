package com.system.petpost.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.service.CommentLikeService;
import com.system.petpost.util.LoginUserUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comment-likes")
@RequiredArgsConstructor
public class CommentLikeController {

        private final CommentLikeService commentLikeService;

        @PostMapping("/{commentId}")
        @RequirePermission("FORUM_LIKE_USE")
        public ResponseEntity<String> likeComment(
                        @PathVariable Long commentId) {

                Long userId = LoginUserUtil.getLoginUserId();

                commentLikeService.likeComment(commentId, userId);

                return ResponseEntity.ok("按讚成功");
        }

        @DeleteMapping("/{commentId}")
        @RequirePermission("FORUM_LIKE_USE")
        public ResponseEntity<String> unlikeComment(
                        @PathVariable Long commentId) {

                Long userId = LoginUserUtil.getLoginUserId();

                commentLikeService.unlikeComment(commentId, userId);

                return ResponseEntity.ok("取消按讚成功");
        }

        @GetMapping("/{commentId}/status")
        @RequirePermission("FORUM_LIKE_USE")
        public ResponseEntity<Boolean> isLiked(
                        @PathVariable Long commentId) {

                Long userId = LoginUserUtil.getLoginUserId();

                return ResponseEntity.ok(
                                commentLikeService.isLiked(commentId, userId));
        }

        @GetMapping("/{commentId}/count")
        public ResponseEntity<Long> countCommentLikes(
                        @PathVariable Long commentId) {

                return ResponseEntity.ok(
                                commentLikeService.countCommentLikes(commentId));
        }
}