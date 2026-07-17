package com.system.shop.dto;

public class PaymentCallbackRequest {
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
