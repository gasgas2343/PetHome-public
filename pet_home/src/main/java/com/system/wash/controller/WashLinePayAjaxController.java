package com.system.wash.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.system.wash.service.WashLinePayService;

@RestController
public class WashLinePayAjaxController {

    private static final Logger log = LoggerFactory.getLogger(WashLinePayAjaxController.class);

    private final WashLinePayService washLinePayService;

    @Value("${app.frontend-base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    public WashLinePayAjaxController(WashLinePayService washLinePayService) {
        this.washLinePayService = washLinePayService;
    }

    @PostMapping("/ajax/pages/WashLinePay/request")
    public String request(@RequestBody(required = false) String body) {
        JSONObject response = new JSONObject();
        try {
            JSONObject obj = body == null || body.isBlank() ? new JSONObject() : new JSONObject(body);
            Integer paymentId = obj.has("paymentId") && !obj.isNull("paymentId") ? obj.getInt("paymentId") : null;
            if (paymentId == null) {
                throw new IllegalArgumentException("paymentId is required");
            }
            log.info("[CONTROLLER REQUEST] Received Line Pay request. paymentId: {}, body: {}", paymentId, body);
            JSONObject info = washLinePayService.requestPayment(paymentId);
            log.info("[CONTROLLER REQUEST] Line Pay request success. paymentId: {}, info: {}", paymentId, info);
            response.put("success", true);
            response.put("message", "OK");
            response.put("info", info);
            response.put("paymentUrl", info.opt("paymentUrl"));
        } catch (Exception e) {
            log.error("[CONTROLLER REQUEST] Line Pay request failed. body: {}, error: {}", body, e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response.toString();
    }

    @GetMapping({"/pay/confirm", "/ajax/pages/WashLinePay/confirm"})
    public RedirectView confirm(
            @RequestParam("orderId") String orderId,
            @RequestParam(value = "transactionId", required = false) Long transactionId) {
        log.info("[CONTROLLER CONFIRM] Received payment confirm. OrderId: {}, TransactionId: {}", orderId, transactionId);
        try {
            org.json.JSONObject response = washLinePayService.confirmPayment(orderId, transactionId);
            boolean success = response != null && "0000".equals(response.optString("returnCode"));
            if (success) {
                com.system.wash.entity.WashLinePayTransactionBean txn = washLinePayService.getTransactionByOrderId(orderId);
                String newPayNo = "";
                int amount = 200;
                int remaining = 0;
                if (txn != null && txn.getPayment() != null) {
                    com.system.wash.entity.WashPaymentBean payment = txn.getPayment();
                    newPayNo = payment.getPayNo() != null ? payment.getPayNo() : "";
                    amount = payment.getAmount() != null ? payment.getAmount() : 200;
                    
                    if (payment.getApptOrder() != null) {
                        Integer total = payment.getApptOrder().getTotalAmount();
                        if (total != null) {
                            remaining = total;
                        }
                    }
                }
                return new RedirectView(frontendBaseUrl + "/wash/payment?linePay=success"
                        + "&amount=" + amount
                        + "&transactionId=" + (transactionId != null ? transactionId : "")
                        + "&orderId=" + java.net.URLEncoder.encode(newPayNo, java.nio.charset.StandardCharsets.UTF_8)
                        + "&remaining=" + remaining);
            } else {
                return new RedirectView(frontendBaseUrl + "/wash/payment?linePay=failed&orderId=" + java.net.URLEncoder.encode(orderId, java.nio.charset.StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            try {
                return new RedirectView(frontendBaseUrl + "/wash/payment?linePay=failed&orderId=" + java.net.URLEncoder.encode(orderId, java.nio.charset.StandardCharsets.UTF_8));
            } catch (Exception ex) {
                return new RedirectView(frontendBaseUrl + "/wash/payment?linePay=failed&orderId=" + orderId);
            }
        }
    }
}
