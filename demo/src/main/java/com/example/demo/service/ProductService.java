package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository ProductRepository;

    public ProductService(ProductRepository ProductRepository) {

        this.ProductRepository = ProductRepository;
    }
    public List<Product> getAllProduct(){

        return ProductRepository.findAll();
    }

    public Optional<Product> getProductByEmail(String email ){
        return ProductRepository.findByEmail(email);

    }
    public Optional<Product> getProductByPhoneNumber(String phoneNumber){
        return ProductRepository.findByPhoneNumber(phoneNumber);
    }
    public Product createProduct(Product product){

        return ProductRepository.save(product);
    }

}
