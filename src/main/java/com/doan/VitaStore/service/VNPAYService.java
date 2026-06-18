package com.doan.VitaStore.service;

import com.doan.VitaStore.config.VNPAYConfig;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPAYService {

    @Autowired
    private VNPAYConfig vnpayConfig;

    public String createPaymentUrl(long amount, String orderInfo, String orderId, String ipAddress) {
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", vnpayConfig.getVersion());
        params.put("vnp_Command", vnpayConfig.getCommand());
        params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", orderId);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
        params.put("vnp_IpAddr", ipAddress);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String createDate = sdf.format(cal.getTime());
        params.put("vnp_CreateDate", createDate);

        cal.add(Calendar.MINUTE, 15);
        String expireDate = sdf.format(cal.getTime());
        params.put("vnp_ExpireDate", expireDate);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                hashData.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(key, StandardCharsets.US_ASCII))
                     .append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                hashData.append('&');
                query.append('&');
            }
        }
        if (hashData.length() > 0) hashData.setLength(hashData.length() - 1);
        if (query.length() > 0) query.setLength(query.length() - 1);

        String secureHash = HmacUtils.hmacSha512Hex(vnpayConfig.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(URLEncoder.encode(secureHash, StandardCharsets.US_ASCII));

        return vnpayConfig.getPayUrl() + "?" + query.toString();
    }

    public boolean verifyIpn(Map<String, String> params) {
        String secureHash = params.get("vnp_SecureHash");
        if (secureHash == null) return false;

        TreeMap<String, String> sorted = new TreeMap<>(params);
        sorted.remove("vnp_SecureHash");
        sorted.remove("vnp_SecureHashType");

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            String value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                hashData.append(entry.getKey()).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                hashData.append('&');
            }
        }
        if (hashData.length() > 0) hashData.setLength(hashData.length() - 1);

        String computedHash = HmacUtils.hmacSha512Hex(vnpayConfig.getHashSecret(), hashData.toString());
        return computedHash.equals(secureHash);
    }
}
