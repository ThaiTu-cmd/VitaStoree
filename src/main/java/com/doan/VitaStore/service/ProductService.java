package com.doan.VitaStore.service;

import com.doan.VitaStore.dto.request.admin.ProductRequest;
import com.doan.VitaStore.dto.response.admin.ProductResponse;
import com.doan.VitaStore.dto.response.client.ShopProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(int id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(int id, ProductRequest request);
    void deleteProduct(int id);
    ProductResponse restoreProduct(int id);

    Page<ShopProductResponse> getShopProducts(String search, List<Integer> categoryIds, String sort, String price, int page);
    long countByPriceRange(long min, long max);
    long countUnderPrice(long max);
    long countOverPrice(long min);
}
