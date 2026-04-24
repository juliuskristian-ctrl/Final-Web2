package com.example.productcrud.service;

import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> getProductsByOwnerPaginated(User owner, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        return productRepository.findByOwner(owner, pageable);
    }

    public Page<Product> getAllProductsPaginated(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        return productRepository.findAll(pageable);
    }


    public Page<Product> searchProductsByOwner(String keyword, Long categoryId, User owner, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword : null;

        return productRepository.searchProductsByOwner(searchKeyword, categoryId, owner, pageable);
    }

    public List<Product> findAllByOwner(User owner) {
        return productRepository.findByOwner(owner);
    }

    public Optional<Product> findByIdAndOwner(Long id, User owner) {
        return productRepository.findByIdAndOwner(id, owner);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteByIdAndOwner(Long id, User owner) {
        productRepository.findByIdAndOwner(id, owner)
                .ifPresent(product -> productRepository.delete(product));
    }
}