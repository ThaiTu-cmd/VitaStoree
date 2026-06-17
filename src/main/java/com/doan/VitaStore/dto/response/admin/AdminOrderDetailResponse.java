package com.doan.VitaStore.dto.response.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AdminOrderDetailResponse {
    private int id; private String orderCode; private int userId;
    private String username; private BigDecimal totalAmount;
    private String status; private String createdAt;
    private String receiverName; private String receiverPhone;
    private String province; private String district; private String ward;
    private String streetAddress; private String paymentMethod;
    private String paymentStatus; private String note;
    private List<Map<String, Object>> items;
    private List<Map<String, Object>> history;

    public AdminOrderDetailResponse() {}

    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public String getOrderCode() { return orderCode; } public void setOrderCode(String v) { this.orderCode = v; }
    public int getUserId() { return userId; } public void setUserId(int v) { this.userId = v; }
    public String getUsername() { return username; } public void setUsername(String v) { this.username = v; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal v) { this.totalAmount = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public String getCreatedAt() { return createdAt; } public void setCreatedAt(String v) { this.createdAt = v; }
    public String getReceiverName() { return receiverName; } public void setReceiverName(String v) { this.receiverName = v; }
    public String getReceiverPhone() { return receiverPhone; } public void setReceiverPhone(String v) { this.receiverPhone = v; }
    public String getProvince() { return province; } public void setProvince(String v) { this.province = v; }
    public String getDistrict() { return district; } public void setDistrict(String v) { this.district = v; }
    public String getWard() { return ward; } public void setWard(String v) { this.ward = v; }
    public String getStreetAddress() { return streetAddress; } public void setStreetAddress(String v) { this.streetAddress = v; }
    public String getPaymentMethod() { return paymentMethod; } public void setPaymentMethod(String v) { this.paymentMethod = v; }
    public String getPaymentStatus() { return paymentStatus; } public void setPaymentStatus(String v) { this.paymentStatus = v; }
    public String getNote() { return note; } public void setNote(String v) { this.note = v; }
    public List<Map<String, Object>> getItems() { return items; } public void setItems(List<Map<String, Object>> v) { this.items = v; }
    public List<Map<String, Object>> getHistory() { return history; } public void setHistory(List<Map<String, Object>> v) { this.history = v; }
}
