package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.model.ProductType;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import com.example.demo.model.User;
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserService userService;

    public ProductService(ProductRepository productRepository, UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
    }
    public List<Product> getAllProduct(){

        return productRepository.findAll();
    }

    // *** НОВИЙ МЕТОД: Видалити продукт за ID з перевіркою прав (якщо потрібно) ***
    @Transactional // Видалення має виконуватися в транзакції
    // Приймає ID продукту та, можливо, ID поточного користувача для перевірки
    public void deleteProductById(Long productId /* , Long currentUserId */) {
        // 1. Знаходимо продукт, який потрібно видалити
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт з ID '" + productId + "' не знайдено для видалення"));

        // 2. *** ДОДАНО: Перевірка авторизації (якщо потрібно) ***
        // Наприклад: лише творець або адмін може видалити продукт
        /*
        User currentUser = userService.getUserById(currentUserId); // Знаходимо поточного користувача

        // Перевірка, чи поточний користувач є творцем продукту АБО чи він є адміном
        // Припускаємо, що у вас є поле 'creator' у Product та метод getRoles() у User
        if (!product.getCreator().getId().equals(currentUserId) && !currentUser.getRoles().contains("ADMIN")) {
            // Якщо користувач не є творцем і не є адміном, кидаємо виняток
            throw new AccessDeniedException("Користувач не має прав для видалення цього продукту.");
            // Або можна кинути кастомний виняток, який обробить GlobalExceptionHandler
        }
        */
        // 3. Видаляємо продукт
        productRepository.deleteById(productId);
    }
    public List<Product> getProductsByType(ProductType type ){ // Змінено тип аргументу
        return productRepository.findByType(type);
    }

    public Product getProductByOriginalTitle(String originalTitle){
        return productRepository.findByOriginalTitle(originalTitle)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт з Original Title '" + originalTitle + "' не знайдено"));
    }
    public Product getProductById(Long id){ // Змінив тип повернення з Optional<Product> на Product
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт з ID '" + id + "' не знайдено"));
    }
    public Product createProduct(Product product){

        return productRepository.save(product);
    }
    @Transactional // Оновлення має виконуватися в межах транзакції
    public Product updateProduct(Long id, Product updatedProductDetails) {
        // 1. Знаходимо існуючий продукт за ID
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт з ID '" + id + "' не знайдено для оновлення"));

        // 2. Оновлюємо поля існуючого продукту з даних, що прийшли в запиті
        // Тут ми просто копіюємо поля. В реальному додатку може знадобитися
        // більш складна логіка або використання DTO для оновлення,
        // щоб уникнути випадкового оновлення ID або інших заборонених полів.
        existingProduct.setOriginalTitle(updatedProductDetails.getOriginalTitle());
        existingProduct.setType(updatedProductDetails.getType());
        existingProduct.setDescription(updatedProductDetails.getDescription());
        existingProduct.setReviewCount(updatedProductDetails.getReviewCount());
        existingProduct.setAverageRating(updatedProductDetails.getAverageRating());
        // Зауваження: оновлення списку коментарів (comments) через цей метод
        // вимагатиме окремої логіки або спеціального DTO, оскільки це зв'язана сутність.
        // Зараз ми не оновлюємо коментарі напряму через цей метод оновлення продукту.


        // 3. Зберігаємо оновлений продукт (Spring Data JPA відстежує зміни в транзакції)
        // Хоча Spring Data JPA може автоматично зберігати зміни в транзакції
        // після модифікації сутності, явний виклик save() є хорошою практикою
        // і повертає оновлену сутність.
        return productRepository.save(existingProduct);
    }
    @Transactional // Видалення має виконуватися в транзакції
    public void clearProductTable() {
        // Варіант 1: deleteAll() - може бути повільнішим для великих таблиць, видаляє сутності по черзі
        // yourEntityRepository.deleteAll();

        // Варіант 2: deleteAllInBatch() - зазвичай швидше, виконує один SQL DELETE запит
        productRepository.deleteAllInBatch();

        System.out.println("Таблиця YourEntity очищена.");
    }

}
