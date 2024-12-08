package com.ecommerce.ecommerce_spring.services;

import com.ecommerce.ecommerce_spring.exceptions.APIException;
import com.ecommerce.ecommerce_spring.exceptions.ResourceNotFoundException;
import com.ecommerce.ecommerce_spring.models.Category;
import com.ecommerce.ecommerce_spring.payload.CategoryDTO;
import com.ecommerce.ecommerce_spring.payload.CategoryResponse;
import com.ecommerce.ecommerce_spring.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()) {
            throw new APIException("No categories created till now");
        }

        List<CategoryDTO> categoriesDTOs = categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoriesDTOs);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null) {
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists");
        }

        Category savedCategoryDTO = categoryRepository.save(category);
        return modelMapper.map(savedCategoryDTO, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategoryById(Long CategoryId) {
        Category category = categoryRepository.findById(CategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", CategoryId));

        categoryRepository.deleteById(CategoryId);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long CategoryId) {

        if (categoryDTO.getCategoryName() == null)
            throw new APIException("Category name is required");

        Category category = categoryRepository.findById(CategoryId).
                orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", CategoryId));

        categoryDTO.setCategoryId(CategoryId);
        Category savedCategoryDTO = modelMapper.map(categoryDTO, Category.class);
        categoryRepository.save(savedCategoryDTO);

        return modelMapper.map(savedCategoryDTO, CategoryDTO.class);
    }
}
