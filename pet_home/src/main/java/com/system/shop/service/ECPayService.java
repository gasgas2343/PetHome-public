package com.system.shop.service;

import com.system.shop.repository.PaymentRepository;
import com.system.shop.config.ECPayConfig;
import com.system.shop.dto.ECPayOrderRequest;
import com.system.shop.entity.Payment;
import com.system.shop.util.ECPayCheckMacValueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ECPayService {

    @Autowired
    private ECPayConfig ecPayConfig;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository; // 需要用來寫入 providerTxId

    public Map<String, String> createOrderParams(ECPayOrderRequest request) {
        Map<String, String> params = new LinkedHashMap<>();

        String merchantTradeNo = "PET" + request.getOrderId() + System.currentTimeMillis() % 100000;
        String tradeDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        params.put("MerchantID", ecPayConfig.getMerchantId());
        params.put("MerchantTradeNo", merchantTradeNo);
        params.put("MerchantTradeDate", tradeDate);
        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(request.getTotalAmount()));
        params.put("TradeDesc", "PetHome寵物用品訂單");
        params.put("ItemName", request.getItemName());
        params.put("ReturnURL", ecPayConfig.getReturnUrl());
        params.put("ClientBackURL", ecPayConfig.getClientBackUrl());
        params.put("ChoosePayment", "Credit");
        params.put("EncryptType", "1");

        // 檢查碼必須在所有「綠界規定的參數」都放好之後計算，
        // actionUrl 不是綠界規定的參數，所以要等檢查碼算完才能加進去
        String checkMacValue = ECPayCheckMacValueUtil.generate(
                params, ecPayConfig.getHashKey(), ecPayConfig.getHashIv());
        params.put("CheckMacValue", checkMacValue);

        // 關鍵：在導向綠界之前，先建立一筆 pending 的 Payment 記錄，
        // 並把 merchantTradeNo 存進 providerTxId，供 Webhook 回來時查詢比對
        Payment payment = paymentService.createPayment(
                request.getOrderId(), "ECPay", "Credit",
                BigDecimal.valueOf(request.getTotalAmount()));
        payment.setProviderTxId(merchantTradeNo);
        paymentRepository.save(payment);

        // 額外附加 actionUrl 給前端使用（動態組 form 時要知道要 POST 去哪裡）。
        // 必須在 CheckMacValue 算完之後才放進 params，
        // 否則會被誤當成參數的一部分一起送去綠界，導致驗證失敗
        params.put("actionUrl", ecPayConfig.getActionUrl());

        return params;
    }

    public boolean verifyNotify(Map<String, String> params) {
        String receivedCheckMacValue = params.get("CheckMacValue");
        Map<String, String> paramsForVerify = new LinkedHashMap<>(params);
        paramsForVerify.remove("CheckMacValue");

        String calculatedCheckMacValue = ECPayCheckMacValueUtil.generate(
                paramsForVerify, ecPayConfig.getHashKey(), ecPayConfig.getHashIv());

        return calculatedCheckMacValue.equals(receivedCheckMacValue);
    }
}