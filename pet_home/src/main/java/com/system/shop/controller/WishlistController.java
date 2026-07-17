package com.system.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.shop.dto.Result;
import com.system.shop.entity.Wishlist;
import com.system.shop.service.WishlistService;

import java.util.List;

@Tag(name = "收藏清單", description = "加入、移除、查詢收藏")
@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired private WishlistService wishlistService;

    @Operation(summary = "查詢收藏清單")
    @GetMapping("/{userId}")
    public Result<List<Wishlist>> getWishlist(
            @Parameter(description = "用戶 ID", example = "1") @PathVariable Long userId) {
        return Result.success(wishlistService.getWishlist(userId));
    }

    @Operation(summary = "加入收藏", description = "商品不存在或已收藏會報錯")
    @PostMapping("/{userId}/product/{productId}")
    public Result<Wishlist> add(@PathVariable Long userId, @PathVariable Integer productId) {
        return Result.success(wishlistService.addToWishlist(userId, productId));
    }

    @Operation(summary = "移除收藏")
    @DeleteMapping("/{userId}/product/{productId}")
    public Result<String> remove(@PathVariable Long userId, @PathVariable Integer productId) {
        wishlistService.removeFromWishlist(userId, productId);
        return Result.success("已移除收藏");
    }

    @Operation(summary = "檢查是否已收藏", description = "回傳 true / false")
    @GetMapping("/{userId}/product/{productId}/check")
    public Result<Boolean> check(@PathVariable Long userId, @PathVariable Integer productId) {
        return Result.success(wishlistService.isWishlisted(userId, productId));
    }
}
