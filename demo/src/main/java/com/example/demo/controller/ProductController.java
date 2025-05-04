package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.model.ProductType;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid; // Для валідації @RequestBody
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // Використовуємо ResponseEntity для контролю над статусом
import org.springframework.web.bind.annotation.*;
import java.util.List;
// import java.util.Optional; // Не використовуємо Optional у контролері, краще обробляти в сервісі

@RestController
//@CrossOrigin(origins = "http://localhost:3000") // CORS тепер налаштовуємо глобально в SecurityConfig
@RequestMapping("/products") // Базовий шлях для ендпоінтів продуктів
public class ProductController {

    private final ProductService productService;

    // Впровадження залежностей через конструктор
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Тестовий публічний ендпоінт (якщо потрібен)
    @GetMapping("/hel")
    public String sayHello() {
        return "Привіт з беку";
    }

    // Отримати список усіх продуктів
    // Зазвичай, цей ендпоінт може бути публічним (permitAll()) або вимагати аутентифікації.
    @GetMapping // GET /products
    public ResponseEntity<List<Product>> getAllProducts() { // Використовуємо множину "Products" у назві методу
        List<Product> products = productService.getAllProduct();
        // *** Важливо: У реальному додатку, можливо, варто повернути List<ProductDto> замість List<Product>,
        // щоб не розкривати зайві дані (наприклад, список коментарів, якщо він не потрібен).
        return ResponseEntity.ok(products); // Повертаємо 200 OK та список продуктів
    }

    // Отримати продукт за його ID
    // Цей ендпоінт також може бути публічним або захищеним залежно від потреб.
    @GetMapping("/{id}") // GET /products/{id}
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            // Сервіс кидає ResourceNotFoundException, якщо продукт не знайдено.
            // GlobalExceptionHandler обробить цей виняток і поверне 404 Not Found.
            Product product = productService.getProductById(id);
            // *** Важливо: Повернути ProductDto, якщо не хочеш показувати всі деталі (наприклад, comments) ***
            return ResponseEntity.ok(product); // Повертаємо 200 OK та продукт
        } catch (ResourceNotFoundException e) {
            // Викидаємо виняток, щоб він був перехоплений GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Загальна помилка 500
        }
    }

    // Отримати список продуктів за типом
    // Цей ендпоінт може бути публічним або захищеним.
    @GetMapping("/by-type") // GET /products/by-type?type=ANIME
    public ResponseEntity<List<Product>> getProductsByType(@RequestParam ProductType type) { // Використовуємо @RequestParam
        List<Product> products = productService.getProductsByType(type);
        // *** Важливо: Повернути List<ProductDto> ***
        return ResponseEntity.ok(products); // Повертаємо 200 OK та список продуктів
    }

    // Отримати продукт за оригінальною назвою
    // Змінюємо на GET запит з параметром запиту
    // Цей ендпоінт може бути публічним або захищеним.
    @GetMapping("/by-title") // GET /products/by-title?title=Назва Продукту
    public ResponseEntity<Product> getProductByOriginalTitle(@RequestParam("title") String originalTitle) { // Використовуємо @RequestParam з явною назвою параметра
        try {
            // Сервіс кидає ResourceNotFoundException, якщо продукт не знайдено.
            Product product = productService.getProductByOriginalTitle(originalTitle);
            // *** Важливо: Повернути ProductDto ***
            return ResponseEntity.ok(product); // Повертаємо 200 OK та продукт
        } catch (ResourceNotFoundException e) {
            // Викидаємо виняток, щоб GlobalExceptionHandler його обробив
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Загальна помибка 500
        }
    }


    // Створити новий продукт
    // Цей ендпоінт має бути захищеним і, можливо, доступним лише для користувачів з певною роллю (наприклад, ADMIN).
    // Видаляємо дублюючий метод createProduct2 (з поверненням boolean)
    // Перейменовуємо saveTest на save або просто залишаємо /
    @PostMapping // POST /products
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product productDetails) { // Використовуємо @Valid для валідації
        try {
            // *** Важливо: У реальному додатку, можливо, варто приймати ProductCreationDto,
            // щоб уникнути можливості передачі ID, reviewCount, averageRating, comments з фронтенду при створенні.
            // Також потрібно отримати поточного користувача з SecurityContext,
            // щоб встановити його як "creator" продукту (як ми додавали зв'язок).

            Product createdProduct = productService.createProduct(productDetails);
            // Повертаємо 201 Created та створений продукт
            // Використовуємо created(), який встановлює статус 201 і додає заголовок Location
            return ResponseEntity.created(null).body(createdProduct); // Можна вказати URI нового ресурсу в Location

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Загальна помилка 500
        }
    }

    // Оновити існуючий продукт за його ID
    // Цей ендпоінт має бути захищеним і, можливо, доступним лише для користувачів з певною роллю (наприклад, ADMIN).
    @PutMapping("/{id}") // PUT /products/{id}
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id, // Отримуємо ID з URL шляху
            @Valid @RequestBody Product updatedProductDetails // Отримуємо оновлені дані з тіла запиту
    ) {
        // *** Важливо: У реальному додатку, можливо, варто приймати ProductUpdateDto,
        // щоб мати контроль над оновлюваними полями.
        // Також, якщо продукт має творця, потрібно перевірити права доступу
        // (чи поточний користувач є творцем або адміном).
        try {
            // Валідація @RequestBody буде виконана автоматично завдяки @Valid
            // Сервіс кидає ResourceNotFoundException, якщо продукт не знайдено.
            Product updatedProduct = productService.updateProduct(id, updatedProductDetails);
            // *** Важливо: Повернути ProductDto ***
            return ResponseEntity.ok(updatedProduct); // Повертаємо 200 OK та оновлений продукт
        } catch (ResourceNotFoundException e) {
            // Викидаємо виняток для GlobalExceptionHandler (404 Not Found)
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Загальна помилка 500
        }
    }

    // Можна додати ендпоінт для видалення продукту (DELETE /{id})
    // Цей ендпоінт має бути захищеним і доступним лише для адмінів або творця продукту.
    // @DeleteMapping("/{id}")
    // public ResponseEntity<?> deleteProduct(@PathVariable Long id) { ... }
}