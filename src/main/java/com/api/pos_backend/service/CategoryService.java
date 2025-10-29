package com.api.pos_backend.service;

import com.api.pos_backend.dto.CategoryDTO;
import com.api.pos_backend.shared.pagination.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    PageResponse<CategoryDTO> getAllCategories(Pageable pageable);

    PageResponse<CategoryDTO> getCategoryById(Long id);

    PageResponse<CategoryDTO> getCategoryByName(String name);

    PageResponse<CategoryDTO> createCategory(CategoryDTO categoryDTO);

    PageResponse<CategoryDTO> updateCategory(Long id, CategoryDTO name);

    String deleteCategory(Long id);

}
