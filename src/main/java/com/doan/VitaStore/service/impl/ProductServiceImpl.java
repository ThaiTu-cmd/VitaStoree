package com.doan.VitaStore.service.impl;

import com.doan.VitaStore.dto.request.admin.ProductRequest;
import com.doan.VitaStore.dto.response.admin.ProductResponse;
import com.doan.VitaStore.entity.CategoriesEntity;
import com.doan.VitaStore.entity.ProductsEntity;
import com.doan.VitaStore.enums.ProductStatus;
import com.doan.VitaStore.repository.CategoriesRepository;
import com.doan.VitaStore.repository.ProductsRepository;
import com.doan.VitaStore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductsRepository productRepository;

    @Autowired
    private CategoriesRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(int id) {
        ProductsEntity product = productRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        return toResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        ProductsEntity product = new ProductsEntity();

        product.setProductName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getStockQuantity());
        product.setImageURL(request.getImageUrl());
        product.setStatus(ProductStatus.valueOf(request.getStatus()));
        product.setDeletedAt(null);

        if (request.getCategoryId() > 0) {
            CategoriesEntity category = categoryRepository.findById((long) request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
            product.setCategory(category);
        }

        ProductsEntity saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Override
    public ProductResponse updateProduct(int id, ProductRequest request) {
        ProductsEntity product = productRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        product.setProductName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getStockQuantity());
        product.setImageURL(request.getImageUrl());
        product.setStatus(ProductStatus.valueOf(request.getStatus()));

        if (request.getCategoryId() > 0) {
            CategoriesEntity category = categoryRepository.findById((long) request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
            product.setCategory(category);
        }

        ProductsEntity saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Override
    public void deleteProduct(int id) {
        ProductsEntity product = productRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Override
    public ProductResponse restoreProduct(int id) {
        ProductsEntity product = productRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        product.setDeletedAt(null);
        return toResponse(productRepository.save(product));
    }

    private ProductResponse toResponse(ProductsEntity product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory() != null ? product.getCategory().getCategoryId() : 0,
                product.getCategory() != null ? product.getCategory().getCategoryName() : null,
                product.getQuantity(),
                product.getImageURL(),
                product.getStatus() != null ? product.getStatus().name() : null,
                product.getDeletedAt() != null ? product.getDeletedAt().toString() : null,
                product.getCategory() != null && product.getCategory().getDeletedAt() != null
                        ? product.getCategory().getDeletedAt().toString() : null
        );
    }
}
