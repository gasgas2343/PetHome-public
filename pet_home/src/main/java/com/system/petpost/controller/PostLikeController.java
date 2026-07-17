package com.system.petpost.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.service.PostLikeService;
import com.system.petpost.util.LoginUserUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/post-likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}")
    @RequirePermission("FORUM_LIKE_USE")
    public ResponseEntity<String> likePost(
            @PathVariable Long postId) {

        Long userId = LoginUserUtil.getLoginUserId();

        postLikeService.likePost(postId, userId);

        return ResponseEntity.ok("按讚成功");
    }

    @DeleteMapping("/{postId}")
    @RequirePermission("FORUM_LIKE_USE")
    public ResponseEntity<String> unlikePost(
            @PathVariable Long postId) {

        Long userId = LoginUserUtil.getLoginUserId();

        postLikeService.unlikePost(postId, userId);

        return ResponseEntity.ok("取消按讚成功");
    }

    @GetMapping("/{postId}/status")
    @RequirePermission("FORUM_LIKE_USE")
    public ResponseEntity<Boolean> isLiked(
            @PathVariable Long postId) {

        Long userId = LoginUserUtil.getLoginUserId();

        return ResponseEntity.ok(
                postLikeService.isLiked(postId, userId));
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<Long> countPostLikes(
            @PathVariable Long postId) {

        return ResponseEntity.ok(
                postLikeService.countPostLikes(postId));
    }
}