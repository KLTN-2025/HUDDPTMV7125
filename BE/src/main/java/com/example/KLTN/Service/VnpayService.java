package com.example.KLTN.Service;



import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
@Service

public class VnpayService {

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.url.pay}")
    private String vnpayPayUrl;

    @Value("${vnpay.version}")
    private String version;

    @Value("${vnpay.command}")
    private String command;

    @Value("${vnpay.currency}")
    private String currency;

    @Value("${vnpay.locale}")
    private String locale;

    public String createRedirectUrl(HttpServletRequest request, long amount, String orderInfo, String orderType) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : ":"+request.getServerPort());
        String returnUrl = baseUrl + "/vnpay-return";

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", version);
        vnpParams.put("vnp_Command", command);
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amount * 100)); // nhân 100 như yêu cầu VNPAY
        vnpParams.put("vnp_CurrCode", currency);
        vnpParams.put("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", orderType);
        vnpParams.put("vnp_Locale", locale);
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        vnpParams.put("vnp_IpAddr", request.getRemoteAddr());

        // sắp xếp tham số theo tăng dần key
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = vnpParams.get(fieldName);
            if (value != null && value.length() > 0) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8))
                        .append('&');
                query.append(fieldName).append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8))
                        .append('&');
            }
        }
        // Remove last '&'
        if (hashData.length() > 0) hashData.deleteCharAt(hashData.length()-1);
        if (query.length() > 0) query.deleteCharAt(query.length()-1);

        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);
        System.out.println("VNPAY URL: " + vnpayPayUrl);
        return vnpayPayUrl + "?" + query.toString();
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            hmac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * hashBytes.length);
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while generating vnp_SecureHash", e);
        }
    }

    public boolean validateReturn(Map<String, String> params) {
        // Lấy các tham số trả về, bỏ vnp_SecureHash & vnp_SecureHashType rồi tạo hashData giống như bên gửi
        // so sánh với vnp_SecureHash gửi về, nếu trùng = dữ liệu hợp lệ
        String secureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);
            if (value != null && value.length() > 0) {
                hashData.append(fieldName).append('=').append(value).append('&');
            }
        }
        if (hashData.length() > 0) hashData.deleteCharAt(hashData.length()-1);

        String calculatedHash = hmacSHA512(hashSecret, hashData.toString());
        return calculatedHash.equals(secureHash);
    }
}
