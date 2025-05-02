package com.example.demo.service;

import com.example.demo.model.Product;
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

    public List<Product> getProductByType(String type ){
        return productRepository.findByType(type);

    }
    public Optional<Product> getProductByOriginalTitle(String originalTitle){
        return productRepository.findByOriginalTitle(originalTitle);
    }
    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id);
    }
    public Product createProduct(Product product){

        return productRepository.save(product);
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
