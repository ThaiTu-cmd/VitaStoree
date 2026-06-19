package com.doan.VitaStore.controller.client;

import com.doan.VitaStore.dto.response.client.PaymentStatusResponse;
import com.doan.VitaStore.enums.Role;
import com.doan.VitaStore.security.service.UserDetailsImpl;
import com.doan.VitaStore.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentApiController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/orders/{orderId}/status")
    public ResponseEntity<?> getOrderPaymentStatus(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable int orderId) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Chưa đăng nhập"));
        }
        if (principal.getUser().getRole() == Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Tính năng này không dành cho Admin"));
        }
        try {
            PaymentStatusResponse response = paymentService.getPaymentStatus(
                    orderId, principal.getUser().getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
