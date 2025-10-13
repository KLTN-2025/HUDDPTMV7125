package com.example.KLTN.Config.vnpayConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VnpayConfig {

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.apiUrl}")
    private String apiUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    private final String version = "2.1.0";
    private final String command = "pay";
    private final String currCode = "VND";
    private final String locale = "vn";

    public String getTmnCode() { return tmnCode; }
    public String getHashSecret() { return hashSecret; }
    public String getApiUrl() { return apiUrl; }
    public String getReturnUrl() { return returnUrl; }
    public String getVersion() { return version; }
    public String getCommand() { return command; }
    public String getCurrCode() { return currCode; }
    public String getLocale() { return locale; }
}
