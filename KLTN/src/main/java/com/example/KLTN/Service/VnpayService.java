package com.example.KLTN.Service;



import com.example.KLTN.Config.vnpayConfig.VnpayConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service

public class VnpayService {
    private final VnpayConfig config;

    public VnpayService(VnpayConfig config) {
        this.config = config;
    }

    public String createPaymentUrl(long amount, String orderInfo, HttpServletRequest request) {
        try {
            String ipAddr = getClientIp(request);
            String txnRef = String.valueOf(System.currentTimeMillis());

            Map<String, String> params = new HashMap<>();
            params.put("vnp_Version", config.getVersion());
            params.put("vnp_Command", config.getCommand());
            params.put("vnp_TmnCode", config.getTmnCode());
            params.put("vnp_Amount", String.valueOf(amount * 100));
            params.put("vnp_CurrCode", config.getCurrCode());
            params.put("vnp_TxnRef", txnRef);
            params.put("vnp_OrderInfo", orderInfo.trim().replaceAll("\\s+", " "));
            params.put("vnp_Locale", config.getLocale());
            params.put("vnp_ReturnUrl", config.getReturnUrl());
            params.put("vnp_IpAddr", ipAddr);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            params.put("vnp_CreateDate", LocalDateTime.now().format(formatter));

            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                String fieldValue = params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=').append(fieldValue);
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8))
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                    if (i != fieldNames.size() - 1) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }

            String secureHash = hmacSHA512(config.getHashSecret(), hashData.toString());
            query.append("&vnp_SecureHash=").append(secureHash);

            return config.getApiUrl() + "?" + query;
        } catch (Exception e) {
            throw new RuntimeException("Error creating payment URL", e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return ip;
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac.init(secretKey);
        byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }
}
