package com.doan.VitaStore.controller;

import com.doan.VitaStore.service.PaymentService;
import com.doan.VitaStore.service.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/vnpay")
public class VNPAYController {

    @Autowired
    private VNPAYService vnpayService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/return")
    public String returnUrl(HttpServletRequest request, Model model) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String key = paramNames.nextElement();
            params.put(key, request.getParameter(key));
        }

        boolean isValid = vnpayService.verifyIpn(params);
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");
        String orderId = params.get("vnp_TxnRef");
        String amount = params.get("vnp_Amount");

        if (isValid && "00".equals(responseCode)) {
            try {
                int orderIdInt = Integer.parseInt(orderId);
                paymentService.updatePaymentStatus(orderIdInt, transactionNo, "SUCCESS");
            } catch (Exception ignored) {}
            model.addAttribute("success", true);
            model.addAttribute("message", "Thanh toán VNPAY thành công!");
        } else {
            if (isValid) {
                try {
                    int orderIdInt = Integer.parseInt(orderId);
                    paymentService.updatePaymentStatus(orderIdInt, transactionNo, "FAILED");
                } catch (Exception ignored) {}
            }
            model.addAttribute("success", false);
            model.addAttribute("message", "Thanh toán thất bại hoặc bị hủy (Mã: " + responseCode + ")");
        }

        model.addAttribute("orderId", orderId);
        model.addAttribute("transactionNo", transactionNo);
        model.addAttribute("amount", amount != null ? Long.parseLong(amount) / 100 : 0);
        return "client/views/vnpay-return";
    }

    @GetMapping("/ipn")
    @ResponseBody
    public String ipn(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String key = paramNames.nextElement();
            params.put(key, request.getParameter(key));
        }

        boolean isValid = vnpayService.verifyIpn(params);
        if (!isValid) {
            return "{\"RspCode\":\"97\",\"Message\":\"Invalid signature\"}";
        }

        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");
        String orderId = params.get("vnp_TxnRef");

        try {
            int orderIdInt = Integer.parseInt(orderId);
            if ("00".equals(responseCode)) {
                paymentService.updatePaymentStatus(orderIdInt, transactionNo, "SUCCESS");
                return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
            } else {
                paymentService.updatePaymentStatus(orderIdInt, transactionNo, "FAILED");
                return "{\"RspCode\":\"00\",\"Message\":\"Payment failed\"}";
            }
        } catch (Exception e) {
            return "{\"RspCode\":\"99\",\"Message\":\"Order not found\"}";
        }
    }
}
