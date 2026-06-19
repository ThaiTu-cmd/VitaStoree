package com.doan.VitaStore.dto.response.client;

import java.math.BigDecimal;

public class ShopProductResponse {
    private int id;
    private String name;
    private String imageUrl;
    private String categoryName;
    private BigDecimal price;
    private String brand;
    private int discountPercent;
    private int reviewCount;
    private boolean isNew;
    private BigDecimal originalPrice;
    private int stockQuantity;

    public ShopProductResponse() {}

    public ShopProductResponse(int id, String name, String imageUrl, String categoryName, BigDecimal price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
        this.price = price;
        this.brand = "";
        this.discountPercent = 0;
        this.reviewCount = 0;
        this.isNew = false;
        this.originalPrice = null;
        this.stockQuantity = stockQuantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public boolean isNew() { return isNew; }
    public void setNew(boolean aNew) { isNew = aNew; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
}
