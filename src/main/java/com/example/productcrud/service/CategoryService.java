package com.example.productcrud.service;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Tampilkan HANYA kategori milik user yang sedang login
    public List<Category> findByUser(User user) {
        return categoryRepository.findByUser(user);
    }

    // Cari kategori HANYA milik user yang sedang login
    public Optional<Category> findByIdAndUser(Long id, User user) {
        return categoryRepository.findByIdAndUser(id, user);
    }

    // Simpan kategori dengan mengaitkannya ke user yang sedang login
    public void save(Category category, User user) {
        category.setUser(user);
        categoryRepository.save(category);
    }

    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    public Object findAll() {
        return null;
    }
}