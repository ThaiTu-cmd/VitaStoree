package com.doan.VitaStore.dto.response.client;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    private int id;
    private int userId;
    private String userName;
    private String createdAt;
    private int itemCount;
    private BigDecimal totalAmount;
    private List<CartItemResponse> items;

    public CartResponse() {}

    public CartResponse(int id, int userId, String userName, String createdAt, int itemCount, BigDecimal totalAmount, List<CartItemResponse> items) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.createdAt = createdAt;
        this.itemCount = itemCount;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }
}
