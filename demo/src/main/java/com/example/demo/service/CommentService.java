package com.example.demo.service;

import com.example.demo.model.Comment;
import com.example.demo.model.Product;
import com.example.demo.model.User; // Припустимо, що у тебе є User модель
import com.example.demo.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
// Імпортуємо наш кастомний виняток
import com.example.demo.exception.ResourceNotFoundException;
import java.util.Optional; // Не забуваємо про імпорт Optional, якщо він десь ще використовується


@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProductService productService; // Потрібен, щоб знайти продукт за ID
    private final UserService userService; // Потрібен, щоб знайти автора за ID (або отримати поточного користувача)


    // Впроваджуємо репозиторій та інші сервіси
    public CommentService(CommentRepository commentRepository, ProductService productService, UserService userService) {
        this.commentRepository = commentRepository;
        this.productService = productService;
        this.userService = userService;
    }

    // Метод для створення нового коментаря
    @Transactional // Операція створення також може бути транзакційною
    public Comment createComment(Long productId, String commentText, Long authorId) {
        // 1. Знаходимо продукт, до якого додається коментар
        // *** ВИПРАВЛЕНО: Прибрано .orElseThrow(), оскільки getProductById вже кидає виняток ***
        Product product = productService.getProductById(productId);

        // 2. Знаходимо користувача, який залишає коментар (автора)
        // *** ВИПРАВЛЕНО: Прибрано .orElseThrow(), оскільки getUserById вже кидає виняток ***
        User author = userService.getUserById(authorId);

        // 3. Створюємо новий об'єкт коментаря
        Comment comment = new Comment();
        comment.setText(commentText);
        comment.setProduct(product); // Встановлюємо продукт
        comment.setAuthor(author);   // Встановлюємо автора
        // createdAt буде встановлено автоматично через @PrePersist у моделі

        // 4. Зберігаємо коментар у базі даних
        return commentRepository.save(comment);
    }

    // Метод для отримання всіх коментарів до певного продукту
    public List<Comment> getCommentsByProductId(Long productId) {
        return commentRepository.findByProductId(productId);
    }

    // Метод для видалення коментаря (за ID коментаря)
    @Transactional
    public boolean deleteCommentById(Long commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
            return true;
        } else {
            return false;
        }
    }

    // Можливо, знадобиться метод для видалення коментаря лише автором або адміністратором
    // @Transactional
    // public boolean deleteCommentByIdIfAuthor(Long commentId, Long userId) {
    //     Optional<Comment> commentOptional = commentRepository.findById(commentId);
    //     if (commentOptional.isPresent()) {
    //         Comment comment = commentOptional.get();
    //         if (comment.getAuthor().getId().equals(userId)) {
    //             commentRepository.deleteById(commentId);
    //             return true;
    //         }
    //     }
    //     return false; // Коментар не знайдено або користувач не є автором
    // }

    // Додай інші методи, якщо потрібно (наприклад, оновлення коментаря)
}