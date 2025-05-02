package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping("/hel")
    public String sayHello(){
        return "Привіт з беку";
    }
    @GetMapping
    public List<Product> getAllProduct(){
        return productService.getAllProduct();
    }


    @PostMapping("/save")
    public boolean createProduct2(@RequestBody Product product) {
        try {
            Product producta = productService.createProduct(product);
            return true;
        } catch (Exception e) {
            System.out.println(product.getType());
            // Обробка інших можливих помилок
            return false;
        }
    }
    @PostMapping("/saveTest")
    public Product createProduct(@RequestBody Product product){
        return productService.createProduct(product);

    }
    @PostMapping("/type")
    public List<Product> getProductByType(@RequestBody String type){
        return productService.getProductByType(type);

    }
    @PostMapping("/originalTitle")
    public Optional<Product> getProductByOriginalTitle(@RequestBody String originalTitle){
        return productService.getProductByOriginalTitle(originalTitle);

    }
}
