package com.example.productcrud.repository;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 1. Mencari semua kategori milik user tertentu
    List<Category> findByUser(User user);

    // 2. Mencari kategori spesifik milik user tertentu (mencegah edit milik orang lain)
    Optional<Category> findByIdAndUser(Long id, User user);
}