package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.ProductType;
import com.example.demo.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/products")
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
    @GetMapping("/by-type") // GET /api/products/by-type?type=ANIME
    public ResponseEntity<List<Product>> getProductsByType(@RequestParam ProductType type){ // Тип аргументу ProductType
        List<Product> products = productService.getProductsByType(type); // Викликаємо оновлений метод сервісу
        return ResponseEntity.ok(products); // Повертаємо 200 OK і список продуктів
    }
    @PostMapping("/originalTitle")
    public Optional<Product> getProductByOriginalTitle(@RequestBody String originalTitle){
        return productService.getProductByOriginalTitle(originalTitle);

    }
}
