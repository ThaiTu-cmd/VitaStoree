package com.doan.VitaStore.dto.response.client;

import com.doan.VitaStore.enums.OrderStatus;
import com.doan.VitaStore.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private int id;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String district;
    private String ward;
    private String streetAddress;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private String note;
    private List<OrderItemResponse> items;

    public String getOrderCode() { return "#DH" + id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public OrderStatus getStatus() { return status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }

    public OrderResponse() {}

    public OrderResponse(int id, LocalDateTime createdAt, OrderStatus status,
                         BigDecimal totalAmount, String receiverName, String receiverPhone,
                         String province, String district, String ward, String streetAddress,
                         String paymentMethod, PaymentStatus paymentStatus, String note,
                         List<OrderItemResponse> items) {
        this.id = id; this.createdAt = createdAt; this.status = status;
        this.totalAmount = totalAmount; this.receiverName = receiverName;
        this.receiverPhone = receiverPhone; this.province = province;
        this.district = district; this.ward = ward; this.streetAddress = streetAddress;
        this.paymentMethod = paymentMethod; this.paymentStatus = paymentStatus;
        this.note = note; this.items = items;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }
    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }

}
