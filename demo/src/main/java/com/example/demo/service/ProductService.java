package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.model.ProductType;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {

        this.productRepository = productRepository;
    }
    public List<Product> getAllProduct(){

        return productRepository.findAll();
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
