// package com.system.member.shop.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.tags.Tag;

// import com.system.member.shop.entity.Order;
// import com.system.member.shop.service.OrderService;
// import com.system.member.shop.dto.OrderCreateRequest;
// import com.system.member.shop.dto.Result; // 確保匯入你的 Result
// import java.util.List;

// @Tag(name = "訂單管理", description = "建立訂單、查詢、更新狀態")
// @RestController
// @RequestMapping("/orders")
// public class OrderController {

//     @Autowired private OrderService orderService;

//     @Operation(summary = "查詢用戶所有訂單", description = "依 userId 查詢，依建立時間倒序")
//     @GetMapping("/user/{userId}")
//     public Result<List<Order>> getUserOrders(
//             @Parameter(description = "用戶 ID", example = "1") @PathVariable Long userId) {
//         return Result.success(orderService.getOrdersByUser(userId));
//     }

//     @Operation(summary = "查詢單筆訂單")
//     @GetMapping("/{orderId}")
//     public Result<Order> getOrder(
//             @Parameter(description = "訂單 ID", example = "1") @PathVariable Integer orderId) {
//         // 異常交由 GlobalExceptionHandler 處理
//         return Result.success(orderService.getOrderById(orderId));
//     }

//     @Operation(summary = "從購物車建立訂單", description = "結帳，可選填優惠券 ID")
//     @PostMapping("/create")
// public Result<Order> createOrder(
//         @RequestBody OrderCreateRequest req) {

//     return Result.success(
//             orderService.createOrderFromCart(req)
//     );
// }

//     @Operation(summary = "更新訂單狀態", description = "可填：Pending / Confirmed / Completed / Cancelled")
//     @PatchMapping("/{orderId}/status")
//     public Result<Order> updateStatus(
//             @PathVariable Integer orderId, 
//             @RequestParam String status) {
//         return Result.success(orderService.updateOrderStatus(orderId, status));
//     }

    
// }

// // 建議移至 com.system.member.shop.dto 套件
// class OrderRequest {
//     private Long userId;
//     private Integer userCouponId;

//     public Long getUserId() { return userId; }
//     public void setUserId(Long userId) { this.userId = userId; }
//     public Integer getUserCouponId() { return userCouponId; }
//     public void setUserCouponId(Integer userCouponId) { this.userCouponId = userCouponId; }
// }
// package com.system.member.shop.controller;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.tags.Tag;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// import com.system.member.shop.dto.OrderCreateRequest;
// import com.system.member.shop.dto.Result;
// import com.system.member.shop.entity.Order;
// import com.system.member.shop.entity.OrderItem;
// import com.system.member.shop.service.OrderService;

// import java.util.List;

// @Tag(name = "訂單管理", description = "建立訂單、查詢、更新狀態")
// @RestController
// @RequestMapping("/orders")
// public class OrderController {

//     @Autowired
//     private OrderService orderService;

//     @Operation(summary = "查詢用戶所有訂單")
//     @GetMapping("/user/{userId}")
//     public Result<List<Order>> getUserOrders(
//             @Parameter(description = "用戶 ID")
//             @PathVariable Long userId) {

//         return Result.success(
//                 orderService.getOrdersByUser(userId)
//         );
//     }

//     @Operation(summary = "查詢單筆訂單")
//     @GetMapping("/{orderId}")
//     public Result<Order> getOrder(
//             @PathVariable Integer orderId) {

//         return Result.success(
//                 orderService.getOrderById(orderId)
//         );
//     }

//     @Operation(summary = "查詢訂單明細")
//     @GetMapping("/{orderId}/items")
//     public Result<List<OrderItem>> getOrderItems(
//             @PathVariable Integer orderId) {

//         return Result.success(
//                 orderService.getOrderItems(orderId)
//         );
//     }

//     @Operation(summary = "從購物車建立訂單")
//     @PostMapping("/create")
//     public Result<Order> createOrder(
//             @RequestBody OrderCreateRequest req) {

//         return Result.success(
//                 orderService.createOrderFromCart(req)
//         );
//     }

//     @Operation(summary = "更新訂單狀態")
//     @PatchMapping("/{orderId}/status")
//     public Result<Order> updateStatus(
//             @PathVariable Integer orderId,
//             @RequestParam String status) {

//         return Result.success(
//                 orderService.updateOrderStatus(
//                         orderId,
//                         status
//                 )
//         );
//     }

//     @Operation(summary = "查詢所有訂單（管理員用）")
// @GetMapping
// public Result<List<Order>> getAllOrders(
//         @RequestParam(required = false) String status) {
//     if (status != null && !status.isEmpty()) {
//         return Result.success(orderService.getOrdersByStatus(status));
//     }
//     return Result.success(orderService.getAllOrders());
// }
// }
package com.system.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.shop.dto.OrderCreateRequest;
import com.system.shop.dto.Result;
import com.system.shop.entity.Order;
import com.system.shop.entity.OrderItem;
import com.system.shop.service.OrderService;
import com.system.member.security.annotation.RequirePermission;
import java.util.List;

@Tag(name = "訂單管理", description = "建立訂單、查詢、更新狀態")
@RestController
// @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "查詢所有訂單（管理員用）")
    @RequirePermission("ORDER_READ_ALL")
    @GetMapping
    public Result<List<Order>> getAllOrders(
            @RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return Result.success(orderService.getOrdersByStatus(status));
        }
        return Result.success(orderService.getAllOrders());
    }

    @Operation(summary = "查詢用戶所有訂單")
    @RequirePermission("ORDER_READ_USER")
    @GetMapping("/user/{userId}")
    public Result<List<Order>> getUserOrders(
            @Parameter(description = "用戶 ID")
            @PathVariable Long userId) {
        return Result.success(orderService.getOrdersByUser(userId));
    }

    @Operation(summary = "查詢單筆訂單")
    @RequirePermission("ORDER_READ_SINGLE")
    @GetMapping("/{orderId}")
    public Result<Order> getOrder(@PathVariable Integer orderId) {
        return Result.success(orderService.getOrderById(orderId));
    }

    @Operation(summary = "查詢訂單明細")
    @RequirePermission("ORDER_READ_ITEMS")
    @GetMapping("/{orderId}/items")
    public Result<List<OrderItem>> getOrderItems(@PathVariable Integer orderId) {
        return Result.success(orderService.getOrderItems(orderId));
    }

    @Operation(summary = "從購物車建立訂單")
    @RequirePermission("ORDER_CREATE")
    @PostMapping("/create")
    public Result<Order> createOrder(@RequestBody OrderCreateRequest req) {
        return Result.success(orderService.createOrderFromCart(req));
    }

    @Operation(summary = "更新訂單狀態")
    @RequirePermission("ORDER_STATUS_UPDATE")
    @PatchMapping("/{orderId}/status")
    public Result<Order> updateStatus(
            @PathVariable Integer orderId,
            @RequestParam String status) {
        return Result.success(orderService.updateOrderStatus(orderId, status));
    }
}