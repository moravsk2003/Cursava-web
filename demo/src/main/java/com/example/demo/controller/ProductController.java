package com.example.demo.controller;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/Product")
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
    public List<Product> getAllUsers(){
        return productService.getAllProduct();
    }


    @PostMapping("/save")
    public boolean createProduct2(@RequestBody Product product){
        try {
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
    @PostMapping("/save2")
    public Product createProduct(@RequestBody Product product){
        return productService.createProduct(product);

    }
    @PostMapping("/email")
    public Optional<Product> getProductByEmail(@RequestBody String email){
        return productService.getProductByEmail(email);

    }
    @PostMapping("/login") // Обробляє POST запити на /api/auth/login
    public boolean authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {

            Optional<Product> a = productService.getProductByEmail(loginRequest.getEmail());
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
    @PostMapping("/phone")
    public Optional<Product> getUserByPhoneNumber(@RequestBody String phoneNumber){
        return productService.getProductByPhoneNumber(phoneNumber);

    }
}
