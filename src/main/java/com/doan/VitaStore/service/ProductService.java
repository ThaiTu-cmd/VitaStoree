package com.doan.VitaStore.service;

import com.doan.VitaStore.dto.request.admin.ProductRequest;
import com.doan.VitaStore.dto.response.admin.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(int id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(int id, ProductRequest request);
    void deleteProduct(int id);
    ProductResponse restoreProduct(int id);
}
