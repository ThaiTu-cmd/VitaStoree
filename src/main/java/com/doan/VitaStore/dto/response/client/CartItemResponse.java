package com.doan.VitaStore.dto.response.client;

import java.math.BigDecimal;

public class CartItemResponse {
    private int id;
    private int productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
    private int quantity;

    public CartItemResponse() {}

    public CartItemResponse(int id, int productId, String productName, BigDecimal price, String imageUrl, int quantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
