package com.doan.VitaStore.service;

import com.doan.VitaStore.dto.response.client.PaymentStatusResponse;
import com.doan.VitaStore.entity.OrdersEntity;
import com.doan.VitaStore.entity.PaymentsEntity;

import java.math.BigDecimal;

public interface PaymentService {
    PaymentsEntity createPendingPayment(OrdersEntity order, String paymentMethod, BigDecimal amount);

    void updatePaymentStatus(int orderId, String transactionNo, String status);

    PaymentStatusResponse getPaymentStatus(int orderId, int userId);
}
