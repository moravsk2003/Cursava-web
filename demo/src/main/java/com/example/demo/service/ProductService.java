package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.model.ProductType;
import com.example.demo.repository.ProductRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.example.demo.model.User;
import com.example.demo.dto.ProductDto;
import com.example.demo.model.User; // Імпорт User для мапінгу creator
import java.util.stream.Collectors; // Імпорт для роботи зі Stream API
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


    @Transactional
    public void deleteProductById(Long productId, Long currentUserId) {
        // 1. Знаходимо продукт, який потрібно видалити
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт з ID '" + productId + "' не знайдено для видалення"));

        // 2. *** ДОДАНО/ЗМІНЕНО: Перевірка авторизації ***
        try {
            User currentUser = userService.getUserById(currentUserId);

            // Перевіряємо, чи користувач є АДМІНОМ
            boolean isAdmin = currentUser.getRoles() != null && currentUser.getRoles().equals("ADMIN"); // Можливо, проблема з цим порівнянням

            // Якщо користувач НЕ є адміном, виконуємо додаткові перевірки
            if (!isAdmin) {
                // *** ВАЖЛИВО: Перевіряємо, чи у продукту ВЗАГАЛІ є творець перед викликом getCreator().getId() ***
                if (product.getCreator() != null) { // <-- ЦЯ ПЕРЕВІРКА МАЄ БУТИ
                    // Якщо творець є, перевіряємо, чи поточний користувач НЕ є цим творцем
                    if (!product.getCreator().getId().equals(currentUserId)) { // <-- Цей рядок або наступний може бути рядком 46
                        throw new AccessDeniedException("Користувач не є творцем продукту і не має прав адміністратора.");
                    }
                } else {
                    // Якщо творця НЕМАЄ (null), і користувач НЕ адмін
                    // -> відмовляємо в доступі (бо видаляти продукти без творця може тільки адмін за цією логікою)
                    throw new AccessDeniedException("Лише адміністратор може видаляти продукти без вказаного творця."); // <-- Інший можливий рядок 46
                }
            }

        } catch (ResourceNotFoundException e) {
            throw new RuntimeException("Помилка авторизації: не знайдено поточного користувача.", e);
        }


        // 3. Видаляємо продукт (якщо авторизація пройшла)
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
    @Transactional // Операція створення також може бути транзакційною
    // Приймає об'єкт Product з даними від фронтенду та ID користувача, який його створив
    public Product createProduct(Product productDetails, Long creatorId) { // Додано creatorId як аргумент
        // 1. Знаходимо користувача-творця за його ID
        User creator = userService.getUserById(creatorId); // Ваш getUserById вже кидає ResourceNotFoundException, якщо користувача не знайдено

        // 2. Встановлюємо знайденого користувача як творця для продукту
        productDetails.setCreator(creator);

        // 3. Можливо, ініціалізуємо інші поля, якщо вони не приходять з фронтенду або мають значення за замовчуванням
        // productDetails.setReviewCount(0);
        // productDetails.setAverageRating(0);
        // productDetails.setComments(new ArrayList<>()); // Коментарі додаються окремо

        // 4. Зберігаємо продукт у базі даних
        return productRepository.save(productDetails);
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
