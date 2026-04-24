package com.example.productcrud.repository;

import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByOwner(User owner);

    Page<Product> findByOwner(User owner, Pageable pageable);

    Optional<Product> findByIdAndOwner(Long id, User owner);

    @Query("SELECT p FROM Product p WHERE p.owner = :owner AND " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CAST(CONCAT('%', :keyword, '%') AS text))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId)")
    List<Product> searchProductsByOwner(@Param("keyword") String keyword,
                                        @Param("categoryId") Long categoryId,
                                        @Param("owner") User owner);
}