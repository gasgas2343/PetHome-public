package com.system.wash.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wash.line-pay")
public class WashLinePayProperties {

    private String apiBaseUrl;
    private String channelId;
    private String channelSecret;
    private String currency = "TWD";
    private String confirmUrl;
    private String cancelUrl;

    public String getApiBaseUrl() { return apiBaseUrl; }
    public void setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; }
    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }
    public String getChannelSecret() { return channelSecret; }
    public void setChannelSecret(String channelSecret) { this.channelSecret = channelSecret; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getConfirmUrl() { return confirmUrl; }
    public void setConfirmUrl(String confirmUrl) { this.confirmUrl = confirmUrl; }
    public String getCancelUrl() { return cancelUrl; }
    public void setCancelUrl(String cancelUrl) { this.cancelUrl = cancelUrl; }
}
