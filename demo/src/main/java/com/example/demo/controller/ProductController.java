package com.example.demo.controller;

import com.example.demo.dto.ProductDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.model.ProductType;
import com.example.demo.model.User;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/products") // Базовий шлях для ендпоінтів продуктів
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    // Впровадження залежностей через конструктор
    public ProductController(ProductService productService,UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }
    // Отримати список усіх продуктів
    @GetMapping // GET /products
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<Product> products = productService.getAllProduct(); // Сервіс повертає List<Product>
        // Мапимо список сутностей на список DTO
        List<ProductDto> productDtos = ProductDto.fromEntityList(products); // Використовуємо статичний метод мапінгу
        return ResponseEntity.ok(productDtos); // Повертаємо 200 OK та список ProductDto
    }
    // Отримати продукт за його ID
    @GetMapping("/{id}") // GET /products/{id}
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) { // Змінено тип повернення
        try {
            Product product = productService.getProductById(id); // Сервіс повертає Product
            // Мапимо сутність на DTO
            ProductDto productDto = ProductDto.fromEntity(product); // Використовуємо статичний метод мапінгу
            return ResponseEntity.ok(productDto); // Повертаємо 200 OK та ProductDto
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Отримати список продуктів за типом
    @GetMapping("/by-type") // GET /products/by-type?type=ANIME
    public ResponseEntity<List<ProductDto>> getProductsByType(@RequestParam ProductType type) {
        List<Product> products = productService.getProductsByType(type);
        // Мапимо список сутностей на список DTO
        List<ProductDto> productDtos = ProductDto.fromEntityList(products); // Використовуємо статичний метод мапінгу
        return ResponseEntity.ok(productDtos); // Повертаємо 200 OK та список ProductDto
    }

    // Повертає ProductDto
    @GetMapping("/by-title") // GET /products/by-title?title=Назва Продукту
    public ResponseEntity<ProductDto> getProductByOriginalTitle(@RequestParam("title") String originalTitle) {
        try {
            Product product = productService.getProductByOriginalTitle(originalTitle);
            // Мапимо сутність на DTO
            ProductDto productDto = ProductDto.fromEntity(product); // Використовуємо статичний метод мапінгу
            return ResponseEntity.ok(productDto); // Повертаємо 200 OK та ProductDto
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // Створити новий продукт
    @PostMapping // POST /products
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody Product productDetails) { // Приймаємо Product (або ProductCreationDto)

        // 1. Отримуємо інформацію про поточного аутентифікованого користувача
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        String currentUsername = ((UserDetails) principal).getUsername();


        try {
            // Знаходимо поточного користувача, щоб отримати його ID
            User currentUser = userService.getUserByEmail(currentUsername);
            Long currentUserId = currentUser.getId();

            // 2. Викликаємо сервісний метод для створення продукту, передаючи дані продукту та ID творця
            Product createdProduct = productService.createProduct(productDetails, currentUserId);

            // 3. Мапимо створений Product на ProductDto перед поверненням
            ProductDto createdProductDto = ProductDto.fromEntity(createdProduct); // Використовуємо статичний метод мапінгу

            // 4. Повертаємо відповідь про успішне створення
            return ResponseEntity.created(null).body(createdProductDto); // Повертаємо ProductDto

        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}") // PUT /products/{id}
    public ResponseEntity<ProductDto> updateProduct( // Повертає ProductDto
                                                     @PathVariable Long id,
                                                     @Valid @RequestBody Product updatedProductDetails // Приймає Product (або ProductUpdateDto)
    )
    {
        try {
            // Сервіс повертає Product
            Product updatedProduct = productService.updateProduct(id, updatedProductDetails);
            // Мапимо оновлений Product на ProductDto перед поверненням
            ProductDto updatedProductDto = ProductDto.fromEntity(updatedProduct); // Використовуємо статичний метод мапінгу
            return ResponseEntity.ok(updatedProductDto); // Повертаємо ProductDto

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}") // HTTP метод DELETE на /products/{id}
     public ResponseEntity<?> deleteProduct(@PathVariable Long id) { // Приймаємо ID продукту з URL
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
         User currentUser;
         try {
             currentUser = userService.getUserByEmail(currentUsername);
         } catch (ResourceNotFoundException e) {
             // Це малоймовірно для аутентифікованого користувача
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not retrieve current user"); // Або 401/403
         }

         try {
             // Викликаємо сервісний метод для видалення, передаючи ID продукту та ID користувача
             productService.deleteProductById(id  , currentUser.getId() ); // Передаємо ID користувача, якщо сервіс його приймає
             // Якщо видалення успішне, повертаємо 204 No Content
             return ResponseEntity.noContent().build();
         } catch (ResourceNotFoundException e) {
             // Якщо продукт не знайдено, повертаємо 404 Not Found (обробляється GlobalExceptionHandler)
             throw e;
         }
         catch (Exception e) {
             e.printStackTrace();
             // Загальна помилка сервера
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the product."); // 500
         }
     }

}