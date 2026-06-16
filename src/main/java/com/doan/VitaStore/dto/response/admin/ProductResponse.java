package com.doan.VitaStore.dto.response.admin;

import java.math.BigDecimal;

public class ProductResponse {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private int categoryId;
    private String categoryName;
    private int stockQuantity;
    private String imageUrl;
    private String status;
    private String deletedAt;
    private String categoryDeletedAt;

    public ProductResponse() {}

    public ProductResponse(int id, String name, String description, BigDecimal price, int categoryId, String categoryName, int stockQuantity, String imageUrl, String status, String deletedAt, String categoryDeletedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.status = status;
        this.deletedAt = deletedAt;
        this.categoryDeletedAt = categoryDeletedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeletedAt() { return deletedAt; }
    public void setDeletedAt(String deletedAt) { this.deletedAt = deletedAt; }
    public String getCategoryDeletedAt() { return categoryDeletedAt; }
    public void setCategoryDeletedAt(String categoryDeletedAt) { this.categoryDeletedAt = categoryDeletedAt; }
}
