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
    Page<Product> searchProductsByOwner(@Param("keyword") String keyword,
                                        @Param("categoryId") Long categoryId,
                                        @Param("owner") User owner,
                                        Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.owner = :owner")
    long countByOwner(@Param("owner") User owner);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.owner = :owner AND p.active = true")
    long countActiveByOwner(@Param("owner") User owner);

    @Query("SELECT COALESCE(SUM(p.price * p.stock), 0) FROM Product p WHERE p.owner = :owner")
    double calculateTotalInventoryValue(@Param("owner") User owner);

    @Query("SELECT p FROM Product p WHERE p.owner = :owner AND p.stock < 5")
    List<Product> findLowStockByOwner(@Param("owner") User owner);

    @Query("SELECT p.category.name, COUNT(p) FROM Product p WHERE p.owner = :owner GROUP BY p.category.name")
    List<Object[]> countProductsByCategory(@Param("owner") User owner);
}