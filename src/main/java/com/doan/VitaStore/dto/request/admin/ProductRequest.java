package com.doan.VitaStore.dto.request.admin;

import java.math.BigDecimal;

public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private int categoryId;
    private int stockQuantity;
    private String imageUrl;
    private String status;

    public ProductRequest() {}

    public ProductRequest(String name, String description, BigDecimal price, int categoryId, int stockQuantity, String imageUrl, String status) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
