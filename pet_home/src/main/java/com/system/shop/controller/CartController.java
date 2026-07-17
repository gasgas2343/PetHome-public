package com.system.shop.controller;

import com.system.member.security.annotation.RequirePermission;
import com.system.shop.dto.CartItemResponse;
import com.system.shop.dto.Result;
import com.system.shop.entity.Cart;
import com.system.shop.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List; // <--- 務必加入這一行
import com.system.member.security.annotation.RequirePermission;

@Tag(name = "購物車", description = "加入、移除、清空購物車")
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired private CartService cartService;

    
    @GetMapping("/user/{userId}")
    @RequirePermission("CART_READ")
    @Operation(summary = "查詢購物車")
public Result<List<CartItemResponse>> getCart(@PathVariable Long userId) {
    return Result.success(cartService.getCartByUser(userId));
}

    @Operation(summary = "加入購物車", description = "相同 SKU 會累加數量，超出庫存會報錯")
    // @PostMapping("/add")
    // public Result<Cart> addToCart(@RequestBody CartRequest req) {
    //     return Result.success(cartService.addToCart(req.getUserId(), req.getProductSkuId(), req.getQuantity()));
    // }
    @RequirePermission("CART_ADD")
    @PostMapping("/add")
public Result<Cart> addToCart(@RequestBody CartRequest req) {
    return Result.success(cartService.addToCart(
        req.getUserId(), req.getProductSkuId(), req.getQuantity(), req.getUnitPrice()
    ));}


    @Operation(summary = "移除購物車單項商品")
    @RequirePermission("CART_ITEM_DELETE")
    @DeleteMapping("/{userId}/sku/{productSkuId}")
    public Result<String> removeItem(
            @PathVariable Long userId, @PathVariable Integer productSkuId) {
        cartService.removeFromCart(userId, productSkuId);
        return Result.success("已移除");
    }

    @Operation(summary = "清空購物車")
    @RequirePermission("CART_CLEAR")
    @DeleteMapping("/{userId}/clear")
    public Result<String> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return Result.success("購物車已清空");
    }
    
}


// 建議：CartRequest 如果會被多個 Controller 共用，建議移到 dto 套件中
class CartRequest {
    private Long userId;
    private Integer productSkuId;
    private Integer quantity = 1;
    private java.math.BigDecimal unitPrice; // ← 新增

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getProductSkuId() { return productSkuId; }
    public void setProductSkuId(Integer productSkuId) { this.productSkuId = productSkuId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public java.math.BigDecimal getUnitPrice() { return unitPrice; } // ← 新增
    public void setUnitPrice(java.math.BigDecimal unitPrice) { this.unitPrice = unitPrice; } // ← 新增
}