package com.doan.VitaStore.dto.response.admin;

public class CategoryResponse {
    private int id;
    private String name;
    private String description;
    private int products;
    private String deletedAt;

    public CategoryResponse() {
    }

    public CategoryResponse(int id, String name, String description, int products, String deletedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.products = products;
        this.deletedAt = deletedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProducts() {
        return products;
    }

    public void setProducts(int products) {
        this.products = products;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}

