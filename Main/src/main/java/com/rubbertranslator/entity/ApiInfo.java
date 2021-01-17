package com.rubbertranslator.entity;

public class ApiInfo {

    public String apiKey;
    public String secretKey;

    public ApiInfo(String apiKey, String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
