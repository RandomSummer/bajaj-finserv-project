package com.bajaj.finserv.model;

public class WebhookResponse {
    private String webhookUrl;
    private String accessToken;

    public WebhookResponse() {}

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
