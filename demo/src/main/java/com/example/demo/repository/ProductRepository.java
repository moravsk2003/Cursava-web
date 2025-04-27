package com.example.demo.repository;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository  extends JpaRepository<Product, Long> {
    Optional<Product> findByEmail(String id);

    Optional<Product> findByPhoneNumber(String originalTitle);
}
