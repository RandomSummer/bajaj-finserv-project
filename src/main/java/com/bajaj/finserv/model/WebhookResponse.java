package com.bajaj.finserv.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookResponse {
    @JsonAlias({"webhook", "webhookUrl", "webhook_url", "url", "testWebhookUrl"})
    private String webhookUrl;
    
    @JsonAlias({"accessToken", "access_token", "token"})
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
    
    @Override
    public String toString() {
        return "WebhookResponse{" +
                "webhookUrl='" + webhookUrl + '\'' +
                ", accessToken='" + (accessToken != null ? accessToken.substring(0, Math.min(30, accessToken.length())) + "..." : "null") + '\'' +
                '}';
    }
}
