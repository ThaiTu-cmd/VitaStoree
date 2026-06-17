package com.doan.VitaStore.service.impl;

import com.doan.VitaStore.dto.request.admin.ProductRequest;
import com.doan.VitaStore.dto.response.admin.ProductResponse;
import com.doan.VitaStore.dto.response.client.ShopProductResponse;
import com.doan.VitaStore.entity.CategoriesEntity;
import com.doan.VitaStore.entity.ProductsEntity;
import com.doan.VitaStore.enums.ProductStatus;
import com.doan.VitaStore.repository.CategoriesRepository;
import com.doan.VitaStore.repository.ProductsRepository;
import com.doan.VitaStore.service.ProductService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public Page<ShopProductResponse> getShopProducts(String search, Integer categoryId, String sort, String price, int page) {
        Specification<ProductsEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNull(root.get("deletedAt")));

            if (search != null && !search.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("productName")), "%" + search.toLowerCase() + "%"));
            }

            if (categoryId != null && categoryId > 0) {
                predicates.add(cb.equal(root.get("category").get("categoryId"), categoryId));
            }

            if (price != null && !price.isBlank()) {
                switch (price) {
                    case "under500k":
                        predicates.add(cb.lessThan(root.get("price"), 500000));
                        break;
                    case "500k-1500k":
                        predicates.add(cb.between(root.get("price"), 500000, 1500000));
                        break;
                    case "1500k-3000k":
                        predicates.add(cb.between(root.get("price"), 1500000, 3000000));
                        break;
                    case "over3000k":
                        predicates.add(cb.greaterThan(root.get("price"), 3000000));
                        break;
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sortBy;
        if (sort != null) {
            sortBy = switch (sort) {
                case "name-asc" -> Sort.by("productName").ascending();
                case "name-desc" -> Sort.by("productName").descending();
                case "price-asc" -> Sort.by("price").ascending();
                case "price-desc" -> Sort.by("price").descending();
                default -> Sort.by("productId").descending();
            };
        } else {
            sortBy = Sort.by("productId").descending();
        }

        Pageable pageable = PageRequest.of(page - 1, 12, sortBy);
        return productRepository.findAll(spec, pageable).map(this::toShopResponse);
    }

    @Override
    public long countByPriceRange(long min, long max) {
        return productRepository.countByDeletedAtIsNullAndPriceBetween(BigDecimal.valueOf(min), BigDecimal.valueOf(max));
    }

    @Override
    public long countUnderPrice(long max) {
        return productRepository.countByDeletedAtIsNullAndPriceLessThan(BigDecimal.valueOf(max));
    }

    @Override
    public long countOverPrice(long min) {
        return productRepository.countByDeletedAtIsNullAndPriceGreaterThan(BigDecimal.valueOf(min));
    }

    @Override
    public ProductResponse restoreProduct(int id) {
        ProductsEntity product = productRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        product.setDeletedAt(null);
        return toResponse(productRepository.save(product));
    }

    private ShopProductResponse toShopResponse(ProductsEntity product) {
        String img = product.getImageURL();
        if (img != null && !img.startsWith("http")) {
            img = "/VitaStore" + img;
        }
        return new ShopProductResponse(
                product.getProductId(),
                product.getProductName(),
                img,
                product.getCategory() != null ? product.getCategory().getCategoryName() : null,
                product.getPrice()
        );
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
