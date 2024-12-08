package com.ecommerce.ecommerce_spring.services;

import com.ecommerce.ecommerce_spring.payload.CategoryDTO;
import com.ecommerce.ecommerce_spring.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String order);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategoryById(Long id);
    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long id);
}
