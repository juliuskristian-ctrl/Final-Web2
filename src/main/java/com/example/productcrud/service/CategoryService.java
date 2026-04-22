package com.example.productcrud.service;

import com.example.productcrud.model.Category;
import com.example.productcrud.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Constructor Injection
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Method ini yang tadi dicari oleh ProductController
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}