package com.system.petmap.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.FavoriteCreateRequest;
import com.system.petmap.dto.FavoriteResponse;
import com.system.petmap.entity.Favorite;
import com.system.petmap.service.FavoriteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map/favorites")
@RequiredArgsConstructor
public class FavoriteController {

        private final FavoriteService favoriteService;

        @RequirePermission("FAVORITE_CREATE")
        @PostMapping
        public ApiResponse<Favorite> create(
                        @Valid @RequestBody FavoriteCreateRequest request) {

                return ApiResponse.success(
                                "FAVORITE_CREATE_SUCCESS",
                                "收藏成功",
                                favoriteService.create(request));
        }

        @RequirePermission("FAVORITE_LIST")
        @GetMapping("/{userId}")
        public ApiResponse<List<FavoriteResponse>> findByUserId(
                        @PathVariable Long userId) {

                return ApiResponse.success(
                                "FAVORITE_LIST_SUCCESS",
                                "取得收藏成功",
                                favoriteService.findByUserId(userId));
        }

        @RequirePermission("FAVORITE_DELETE")
        @DeleteMapping("/{userId}/{placeId}")
        public ApiResponse<Void> delete(
                        @PathVariable Long userId,
                        @PathVariable Integer placeId) {

                favoriteService.delete(
                                userId,
                                placeId);

                return ApiResponse.success(
                                "FAVORITE_DELETE_SUCCESS",
                                "取消收藏成功",
                                null);
        }

        @RequirePermission("FAVORITE_CHECK")
        @GetMapping("/check/{userId}/{placeId}")
        public ApiResponse<Boolean> exists(
                        @PathVariable Long userId,
                        @PathVariable Integer placeId) {

                System.out.println("user=" + userId);
                System.out.println("place=" + placeId);

                return ApiResponse.success(
                                "FAVORITE_EXISTS_SUCCESS",
                                "查詢收藏成功",
                                favoriteService.exists(userId, placeId));
        }
}