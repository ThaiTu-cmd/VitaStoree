package com.doan.VitaStore.service.impl;

import com.doan.VitaStore.dto.response.client.PaymentStatusResponse;
import com.doan.VitaStore.entity.OrdersEntity;
import com.doan.VitaStore.entity.PaymentsEntity;
import com.doan.VitaStore.enums.OrderStatus;
import com.doan.VitaStore.enums.PaymentMethod;
import com.doan.VitaStore.enums.PaymentStatus;
import com.doan.VitaStore.repository.OrdersRepository;
import com.doan.VitaStore.repository.PaymentsRepository;
import com.doan.VitaStore.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentsRepository paymentsRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Override
    @Transactional
    public PaymentsEntity createPendingPayment(OrdersEntity order, String paymentMethod, BigDecimal amount) {
        PaymentMethod method = parsePaymentMethod(paymentMethod);

        PaymentsEntity payment = paymentsRepository.findByOrderOrderId(order.getOrderId())
                .orElseGet(PaymentsEntity::new);
        payment.setOrder(order);
        payment.setPaymentMethod(method);
        payment.setAmount(amount);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setTransactionNo(null);
        payment.setPaidAt(null);
        if (payment.getCreatedAt() == null) {
            payment.setCreatedAt(LocalDateTime.now());
        }
        PaymentsEntity savedPayment = paymentsRepository.save(payment);
        order.setPayment(savedPayment);
        return savedPayment;
    }

    @Override
    @Transactional
    public void updatePaymentStatus(int orderId, String transactionNo, String status) {
        PaymentsEntity payment = paymentsRepository.findByOrderOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán cho đơn hàng"));
        PaymentStatus paymentStatus;
        try {
            paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Trạng thái thanh toán không hợp lệ");
        }

        payment.setPaymentStatus(paymentStatus);
        payment.setTransactionNo(transactionNo);
        if (paymentStatus == PaymentStatus.SUCCESS) {
            payment.setPaidAt(LocalDateTime.now());
        }
        paymentsRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatusResponse getPaymentStatus(int orderId, int userId) {
        OrdersEntity order = ordersRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        if (order.getUser().getUserId() != userId) {
            throw new RuntimeException("Không có quyền xem đơn hàng này");
        }

        PaymentsEntity payment = paymentsRepository.findByOrderOrderId(orderId).orElse(null);
        PaymentMethod method = payment != null ? payment.getPaymentMethod() : PaymentMethod.COD;
        PaymentStatus paymentStatus = payment != null ? payment.getPaymentStatus() : PaymentStatus.PENDING;
        BigDecimal amount = payment != null ? payment.getAmount() : order.getTotalAmount();

        return new PaymentStatusResponse(
                order.getOrderId(),
                "#DH" + order.getOrderId(),
                order.getStatus().name(),
                toOrderStatusLabel(order.getStatus()),
                method.name(),
                paymentStatus.name(),
                toPaymentStatusLabel(paymentStatus),
                payment != null ? payment.getTransactionNo() : null,
                amount,
                payment != null ? payment.getPaidAt() : null
        );
    }

    private PaymentMethod parsePaymentMethod(String paymentMethod) {
        try {
            if (paymentMethod != null && !paymentMethod.isBlank()) {
                return PaymentMethod.valueOf(paymentMethod.toUpperCase());
            }
        } catch (Exception ignored) {
        }
        return PaymentMethod.COD;
    }

    private String toOrderStatusLabel(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Chờ xác nhận";
            case CONFIRMED -> "Xác nhận";
            case SHIPPING -> "Đang giao";
            case COMPLETED -> "Hoàn thành";
            case CANCELLED -> "Đã hủy";
        };
    }

    private String toPaymentStatusLabel(PaymentStatus status) {
        return switch (status) {
            case PENDING -> "Chưa thanh toán";
            case SUCCESS -> "Đã thanh toán";
            case FAILED -> "Thanh toán thất bại";
        };
    }
}
