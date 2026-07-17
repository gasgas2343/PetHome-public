package com.system.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Getter
@Configuration
public class ECPayConfig {

    @Value("${ecpay.merchant-id}")
    private String merchantId;

    @Value("${ecpay.hash-key}")
    private String hashKey;

    @Value("${ecpay.hash-iv}")
    private String hashIv;

    @Value("${ecpay.action-url}")
    private String actionUrl;

    @Value("${ecpay.return-url}")
    private String returnUrl;

    @Value("${ecpay.client-back-url}")
    private String clientBackUrl;
}
