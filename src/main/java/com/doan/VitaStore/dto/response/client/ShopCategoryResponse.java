package com.doan.VitaStore.dto.response.client;

public class ShopCategoryResponse {
    private int id;
    private String slug;
    private String name;
    private int products;

    public ShopCategoryResponse() {}

    public ShopCategoryResponse(int id, String slug, String name, int products) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.products = products;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getProducts() { return products; }
    public void setProducts(int products) { this.products = products; }
}
