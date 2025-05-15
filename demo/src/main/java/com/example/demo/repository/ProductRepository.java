package com.example.demo.repository;

import com.example.demo.model.Product;
import com.example.demo.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ProductRepository  extends JpaRepository<Product, Long> {
    List<Product> findByType(ProductType type);
    Optional<Product> findByOriginalTitle(String originalTitle);
}
