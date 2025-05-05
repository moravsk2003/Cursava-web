package com.example.demo.controller;

import com.example.demo.dto.ProductDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.model.ProductType;
import com.example.demo.model.User;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid; // Для валідації @RequestBody
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // Використовуємо ResponseEntity для контролю над статусом
import org.springframework.web.bind.annotation.*;
import java.util.List;
// Імпорти для отримання інформації про аутентифікованого користувача (вже є)
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.demo.service.UserService;

@RestController
//@CrossOrigin(origins = "http://localhost:3000") // CORS тепер налаштовуємо глобально в SecurityConfig
@RequestMapping("/products") // Базовий шлях для ендпоінтів продуктів
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    // Впровадження залежностей через конструктор
    public ProductController(ProductService productService,UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    // Тестовий публічний ендпоінт (якщо потрібен)
    @GetMapping("/hel")
    public String sayHello() {
        return "Привіт з беку";
    }

    // Отримати список усіх продуктів
    // Зазвичай, цей ендпоінт може бути публічним (permitAll()) або вимагати аутентифікації.
    @GetMapping // GET /products
    public ResponseEntity<List<ProductDto>> getAllProducts() { // Змінено тип повернення
        List<Product> products = productService.getAllProduct(); // Сервіс повертає List<Product>
        // Мапимо список сутностей на список DTO
        List<ProductDto> productDtos = ProductDto.fromEntityList(products); // Використовуємо статичний метод мапінгу
        return ResponseEntity.ok(productDtos); // Повертаємо 200 OK та список ProductDto
    }

    // Отримати продукт за його ID
    // Цей ендпоінт також може бути публічним або захищеним залежно від потреб.
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
    // Цей ендпоінт може бути публічним або захищеним.
    // *** ЗМІНЕНО: Повертає список ProductDto ***
    @GetMapping("/by-type") // GET /products/by-type?type=ANIME
    public ResponseEntity<List<ProductDto>> getProductsByType(@RequestParam ProductType type) { // Змінено тип повернення
        List<Product> products = productService.getProductsByType(type); // Сервіс повертає List<Product>
        // Мапимо список сутностей на список DTO
        List<ProductDto> productDtos = ProductDto.fromEntityList(products); // Використовуємо статичний метод мапінгу
        return ResponseEntity.ok(productDtos); // Повертаємо 200 OK та список ProductDto
    }

    // *** ЗМІНЕНО: Повертає ProductDto ***
    @GetMapping("/by-title") // GET /products/by-title?title=Назва Продукту
    public ResponseEntity<ProductDto> getProductByOriginalTitle(@RequestParam("title") String originalTitle) { // Змінено тип повернення
        try {
            Product product = productService.getProductByOriginalTitle(originalTitle); // Сервіс повертає Product
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
    // Цей ендпоінт має бути захищеним і, можливо, доступним лише для користувачів з певною роллю (наприклад, ADMIN).
    // Видаляємо дублюючий метод createProduct2 (з поверненням boolean)
    // Перейменовуємо saveTest на save або просто залишаємо /
    // Цей ендпоінт має бути захищеним і, можливо, доступним лише для користувачів з певною роллю (наприклад, ADMIN).
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
            // Сервіс повертає Product
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

    // *** ЗМІНЕНО: Повертає ProductDto ***
    @PutMapping("/{id}") // PUT /products/{id}
    public ResponseEntity<ProductDto> updateProduct( // Повертає ProductDto
                                                     @PathVariable Long id,
                                                     @Valid @RequestBody Product updatedProductDetails // Приймає Product (або ProductUpdateDto)
    ) {
        // ... (логіка отримання поточного користувача, якщо потрібна авторизація на оновлення) ...
        // Поки що авторизацію на оновлення ми не реалізовували, але якщо вона є, її треба додати тут.

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
         // *** ДОДАНО: Отримання ID поточного аутентифікованого користувача ***
         // Це потрібно, якщо логіка авторизації знаходиться в сервісі і використовує ID користувача
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
         User currentUser;
         try {
             currentUser = userService.getUserByEmail(currentUsername);
         } catch (ResourceNotFoundException e) {
             // Це малоймовірно для аутентифікованого користувача, але обробляємо
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not retrieve current user"); // Або 401/403
         }

         try {
             // Викликаємо сервісний метод для видалення, передаючи ID продукту та ID користувача
             // commentService.updateComment(id, updatedCommentDetails, currentUser.getId()); // Приклад для коментарів

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