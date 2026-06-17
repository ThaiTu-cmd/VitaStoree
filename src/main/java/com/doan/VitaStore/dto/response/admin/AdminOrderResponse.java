package com.doan.VitaStore.dto.response.admin;

import java.math.BigDecimal;

public class AdminOrderResponse {
    private int id;
    private String orderCode;
    private String username;
    private int userId;
    private BigDecimal total;
    private String status;
    private String createdAt;

    public AdminOrderResponse() {}

    public AdminOrderResponse(int id, String orderCode, String username,
                              int userId, BigDecimal total, String status, String createdAt) {
        this.id = id; this.orderCode = orderCode; this.username = username;
        this.userId = userId; this.total = total; this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

}
