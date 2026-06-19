package com.doan.VitaStore.dto.response.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentStatusResponse {
    private int orderId;
    private String orderCode;
    private String orderStatus;
    private String orderStatusLabel;
    private String paymentMethod;
    private String paymentStatus;
    private String paymentStatusLabel;
    private String transactionNo;
    private BigDecimal amount;
    private LocalDateTime paidAt;

    public PaymentStatusResponse() {
    }

    public PaymentStatusResponse(int orderId, String orderCode, String orderStatus,
                                 String orderStatusLabel, String paymentMethod,
                                 String paymentStatus, String paymentStatusLabel,
                                 String transactionNo, BigDecimal amount,
                                 LocalDateTime paidAt) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.orderStatus = orderStatus;
        this.orderStatusLabel = orderStatusLabel;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentStatusLabel = paymentStatusLabel;
        this.transactionNo = transactionNo;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatusLabel() {
        return orderStatusLabel;
    }

    public void setOrderStatusLabel(String orderStatusLabel) {
        this.orderStatusLabel = orderStatusLabel;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatusLabel() {
        return paymentStatusLabel;
    }

    public void setPaymentStatusLabel(String paymentStatusLabel) {
        this.paymentStatusLabel = paymentStatusLabel;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
