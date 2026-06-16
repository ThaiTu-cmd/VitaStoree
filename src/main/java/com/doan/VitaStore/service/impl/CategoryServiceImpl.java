package com.doan.VitaStore.service.impl;

import com.doan.VitaStore.dto.request.admin.CategoryRequest;
import com.doan.VitaStore.dto.response.admin.CategoryResponse;
import com.doan.VitaStore.entity.CategoriesEntity;
import com.doan.VitaStore.entity.ProductsEntity;
import com.doan.VitaStore.repository.CategoriesRepository;
import com.doan.VitaStore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService
{
    @Autowired
    private CategoriesRepository categoriesRepository;

    @Override
    public List<CategoryResponse> getAllCategory() {
        return categoriesRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        CategoriesEntity category = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tìm thấy."));
        return toResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        CategoriesEntity category = new CategoriesEntity();

        category.setCategoryName(request.getName());
        category.setDescription(request.getDescription());
        category.setDeletedAt(null);

        CategoriesEntity savedCategory = categoriesRepository.save(category);
        return toResponse(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(int id, CategoryRequest request) {
        CategoriesEntity category = categoriesRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        category.setCategoryName(request.getName());
        category.setDescription(request.getDescription());

        CategoriesEntity savedCat = categoriesRepository.save(category);
        return toResponse(savedCat);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) {
        CategoriesEntity category = categoriesRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        LocalDateTime now = LocalDateTime.now();
        category.setDeletedAt(now);

        if (category.getProducts() != null) {
            for (ProductsEntity product : category.getProducts()) {
                product.setDeletedAt(now);
            }
        }

        categoriesRepository.save(category);
    }

    @Override
    @Transactional
    public CategoryResponse restoreCategoryById(Long id) {
        CategoriesEntity category = categoriesRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        category.setDeletedAt(null);

        if (category.getProducts() != null) {
            for (ProductsEntity product : category.getProducts()) {
                product.setDeletedAt(null);
            }
        }

        CategoriesEntity savedCat = categoriesRepository.save(category);
        return toResponse(savedCat);
    }

    private CategoryResponse toResponse(CategoriesEntity category){
        return new  CategoryResponse(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getDescription(),
                category.getProducts() != null ? category.getProducts().size() : 0,
                category.getDeletedAt() != null ? category.getDeletedAt().toString() : null
        );
    }
}
