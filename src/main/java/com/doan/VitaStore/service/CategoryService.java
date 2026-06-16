package com.doan.VitaStore.service;


import com.doan.VitaStore.dto.request.admin.CategoryRequest;
import com.doan.VitaStore.dto.response.admin.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategory();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(int id, CategoryRequest request);
    void deleteCategoryById(Long id);
    CategoryResponse restoreCategoryById(Long id);
}
