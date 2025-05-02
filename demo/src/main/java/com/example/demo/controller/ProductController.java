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
    public boolean createProduct2(@RequestBody Product product){
        try {
            Optional<Product> existingProduct = productService.getProductByType(product.getType());
            if (existingProduct.isPresent()) {
                return false;
            }
            if (productService.createProduct(product) != null) {
                return true;
            } else {
                return false;
            }
        }
        catch (Exception e) {
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
    public Optional<Product> getProductByType(@RequestBody String type){
        return productService.getProductByType(type);

    }
    @PostMapping("/login") // Обробляє POST запити на /api/auth/login
    public boolean authenticateProduct(@Valid @RequestBody LoginRequest loginRequest) {
        try {

            Optional<Product> a = productService.getProductByType(loginRequest.getEmail());
            if (a.isPresent()) {
                Product Product = a.get(); // Отримуємо об'єкт User
                // Тепер працюємо з об'єктом user
                // Наприклад, перевіряємо пароль:
                if (loginRequest.getPassword().equals(Product.getType())) {
                    return true;
                }
            } else {
                return false;
            }
            return false;

        } catch (Exception e) {
            // Обробка інших можливих помилок
            return false;
        }
    }
    @PostMapping("/originalTitle")
    public Optional<Product> getProductByOriginalTitle(@RequestBody String originalTitle){
        return productService.getProductByOriginalTitle(originalTitle);

    }
}
