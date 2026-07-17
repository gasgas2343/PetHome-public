package com.system.petpost.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.service.PostFavoriteService;
import com.system.petpost.util.LoginUserUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/post-favorites")
@RequiredArgsConstructor
public class PostFavoriteController {

    private final PostFavoriteService postFavoriteService;

    @PostMapping("/{postId}")
    @RequirePermission("FORUM_FAVORITE_USE")
    public ResponseEntity<String> favoritePost(
            @PathVariable Long postId) {

        Long userId = LoginUserUtil.getLoginUserId();

        postFavoriteService.favoritePost(postId, userId);

        return ResponseEntity.ok("收藏成功");
    }

    @DeleteMapping("/{postId}")
    @RequirePermission("FORUM_FAVORITE_USE")
    public ResponseEntity<String> unfavoritePost(
            @PathVariable Long postId) {

        Long userId = LoginUserUtil.getLoginUserId();

        postFavoriteService.unfavoritePost(postId, userId);

        return ResponseEntity.ok("取消收藏成功");
    }

    @GetMapping("/{postId}/status")
    @RequirePermission("FORUM_FAVORITE_USE")
    public ResponseEntity<Boolean> isFavorited(
            @PathVariable Long postId) {

        Long userId = LoginUserUtil.getLoginUserId();

        return ResponseEntity.ok(
                postFavoriteService.isFavorited(postId, userId));
    }
}