// package com.system.shop.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// import com.system.member.security.annotation.RequirePermission;
// import com.system.shop.dto.Result;
// import com.system.shop.entity.Payment;
// import com.system.shop.service.PaymentService;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;

// import java.math.BigDecimal;
// import java.util.List;

// @Tag(name = "付款管理", description = "建立付款、接收金流回調")
// @RestController
// @RequestMapping("/payments")
// public class PaymentController {

//     @Autowired private PaymentService paymentService;

//     @Operation(summary = "建立付款記錄")
//     @PostMapping("/create")
//     public Result<Payment> create(@RequestBody PaymentRequest req) {
//         // 異常由 GlobalExceptionHandler 統一處理
//         return Result.success(paymentService.createPayment(
//                 req.getOrderId(), req.getProvider(), req.getMethod(), req.getAmount()));
//     }

//     @Operation(summary = "接收金流回呼 (Webhook)", description = "接收第三方金流通知")
//     @PostMapping("/callback")
//     public Result<String> callback(@RequestBody CallbackRequest req) {
//         paymentService.handleCallback(
//                 req.getIdempotencyKey(), req.getProviderTxId(),
//                 req.getRawCallback(), req.getProvider(), req.getEventType());
//         return Result.success("OK");
//     }

//     @Operation(summary = "查詢訂單付款記錄")
//     @GetMapping("/order/{orderId}")
//     public Result<List<Payment>> getByOrder(@PathVariable Integer orderId) {
//         return Result.success(paymentService.getPaymentsByOrderId(orderId));
//     }
//     @Operation(summary = "模擬付款（測試用）")
// @PostMapping("/mock-pay")
// @RequirePermission("PAYMENT_MOCK")
// public Result<Payment> mockPay(@RequestBody PaymentRequest req) {
//     return Result.success(paymentService.mockPay(
//             req.getOrderId(), req.getMethod(), req.getAmount()));
// }

// @Operation(summary = "查詢所有付款記錄（管理者）")
// @RequirePermission("PAYMENT_READ_ALL")
// @GetMapping("/all")
// public Result<List<Payment>> getAll(
//     @RequestParam(required = false) Integer orderId,
//     @RequestParam(required = false) String status
// ) {
//     return Result.success(paymentService.getAllPayments(orderId, status));
// }
// @GetMapping("/test")
// public String test() {
//     return "ok";
// }
// }

// // 建議將以下 DTO 移至 com.system.member.shop.dto 套件
// class PaymentRequest {
//     private Integer orderId;
//     private String provider;
//     private String method;
//     private BigDecimal amount;

//     public Integer getOrderId() { return orderId; }
//     public void setOrderId(Integer orderId) { this.orderId = orderId; }
//     public String getProvider() { return provider; }
//     public void setProvider(String provider) { this.provider = provider; }
//     public String getMethod() { return method; }
//     public void setMethod(String method) { this.method = method; }
//     public BigDecimal getAmount() { return amount; }
//     public void setAmount(BigDecimal amount) { this.amount = amount; }
// }

// class CallbackRequest {
//     private String idempotencyKey;
//     private String providerTxId;
//     private String rawCallback;
//     private String provider;
//     private String eventType;

//     public String getIdempotencyKey() { return idempotencyKey; }
//     public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
//     public String getProviderTxId() { return providerTxId; }
//     public void setProviderTxId(String providerTxId) { this.providerTxId = providerTxId; }
//     public String getRawCallback() { return rawCallback; }
//     public void setRawCallback(String rawCallback) { this.rawCallback = rawCallback; }
//     public String getProvider() { return provider; }
//     public void setProvider(String provider) { this.provider = provider; }
//     public String getEventType() { return eventType; }
//     public void setEventType(String eventType) { this.eventType = eventType; }
// }


package com.system.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.shop.dto.ECPayOrderRequest;
import com.system.shop.dto.Result;
import com.system.shop.entity.Payment;
import com.system.shop.service.ECPayService;
import com.system.shop.service.PaymentService;
import com.system.member.security.annotation.RequirePermission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "付款管理", description = "建立付款、接收金流回調")
@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired private PaymentService paymentService;
    @Autowired private ECPayService ecPayService;

    @Operation(summary = "建立付款記錄")
    @PostMapping("/create")
    public Result<Payment> create(@RequestBody PaymentRequest req) {
        return Result.success(paymentService.createPayment(
                req.getOrderId(), req.getProvider(), req.getMethod(), req.getAmount()));
    }

    @Operation(summary = "接收金流回呼 (Webhook)", description = "接收第三方金流通知（JSON 格式，適用可傳 JSON 的金流）")
    @PostMapping("/callback")
    public Result<String> callback(@RequestBody CallbackRequest req) {
        paymentService.handleCallback(
                req.getIdempotencyKey(), req.getProviderTxId(),
                req.getRawCallback(), req.getProvider(), req.getEventType());
        return Result.success("OK");
    }

    @Operation(summary = "查詢訂單付款記錄")
    @GetMapping("/order/{orderId}")
    public Result<List<Payment>> getByOrder(@PathVariable Integer orderId) {
        return Result.success(paymentService.getPaymentsByOrderId(orderId));
    }

    @Operation(summary = "模擬付款（測試用）")
    @PostMapping("/mock-pay")
    @RequirePermission("PAYMENT_MOCK")
    public Result<Payment> mockPay(@RequestBody PaymentRequest req) {
        return Result.success(paymentService.mockPay(
                req.getOrderId(), req.getMethod(), req.getAmount()));
    }

    @Operation(summary = "查詢所有付款記錄（管理者）")
    @RequirePermission("PAYMENT_READ_ALL")
    @GetMapping("/all")
    public Result<List<Payment>> getAll(
        @RequestParam(required = false) Integer orderId,
        @RequestParam(required = false) String status
    ) {
        return Result.success(paymentService.getAllPayments(orderId, status));
    }

    @GetMapping("/test")
    public String test() {
        return "ok";
    }

    // ========== 以下為綠界 ECPay 專用 ==========

    /**
     * 建立綠界付款訂單，取得導向付款頁所需的完整參數（含檢查碼）
     * 前端拿到這包 Map 後，動態組一個 form 並 submit（POST）到綠界付款頁
     */
    @Operation(summary = "建立綠界付款訂單，取得導向付款頁所需參數")
    @PostMapping("/ecpay/create")
    public Result<Map<String, String>> createEcpayOrder(@RequestBody ECPayOrderRequest request) {
        Map<String, String> params = ecPayService.createOrderParams(request);
        return Result.success(params);
    }

    /**
     * 綠界背景通知（Webhook），付款完成後綠界的伺服器會直接呼叫這支 API
     * 重要：
     * 1. 此路徑必須排除在 JWT 驗證之外（已請同學加白名單）
     * 2. 回傳格式規定為純文字 "1|OK"，不可包成 Result/JSON，
     *    否則綠界會判定通知失敗並持續重送
     * 3. 本機測試時，此網址須透過 ngrok 對外開放
     * 4. 驗證通過後，轉呼叫既有的 paymentService.handleCallback，
     *    沿用同一套付款記錄邏輯，不另外長出一套平行邏輯
     */
    @Operation(summary = "接收綠界付款結果背景通知（Webhook）")
   @PostMapping("/ecpay/notify")
public String ecpayNotify(@RequestParam Map<String, String> params) {
    boolean isValid = ecPayService.verifyNotify(params);

    if (!isValid) {
        return "0|CheckMacValueError";
    }

    String rtnCode = params.get("RtnCode");
    String merchantTradeNo = params.get("MerchantTradeNo"); // 用這個查 Payment，跟建立時存進去的一致

    String eventType = "1".equals(rtnCode) ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED";

    try {
        paymentService.handleCallback(
                merchantTradeNo,   // idempotencyKey：防止綠界重複通知時重複處理
                merchantTradeNo,   // providerTxId：跟 createOrderParams 存的值一致，才查得到
                params.toString(),
                "ECPay",
                eventType
        );
    } catch (RuntimeException e) {
        // 保護措施：萬一真的查無此筆，先記 log，不要讓整支 API 500，
        // 綠界規定不管怎樣都要回 1|OK 或明確的錯誤格式，避免它一直重送
        System.err.println("[ECPay Notify] 找不到對應付款記錄: " + merchantTradeNo);
    }

    return "1|OK";
}
    }


// 建議將以下 DTO 移至 com.system.member.shop.dto 套件
class PaymentRequest {
    private Integer orderId;
    private String provider;
    private String method;
    private BigDecimal amount;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}

class CallbackRequest {
    private String idempotencyKey;
    private String providerTxId;
    private String rawCallback;
    private String provider;
    private String eventType;

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public String getProviderTxId() { return providerTxId; }
    public void setProviderTxId(String providerTxId) { this.providerTxId = providerTxId; }
    public String getRawCallback() { return rawCallback; }
    public void setRawCallback(String rawCallback) { this.rawCallback = rawCallback; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
}