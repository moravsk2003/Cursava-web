package com.example.demo.service;

import com.example.demo.dto.CommentUpdateDto;
import com.example.demo.model.Comment;
import com.example.demo.model.Product;
import com.example.demo.model.User; // Припустимо, що у тебе є User модель
import com.example.demo.repository.CommentRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
// Імпортуємо наш кастомний виняток
import com.example.demo.exception.ResourceNotFoundException;

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
        product.addComment(comment);
        comment.setAuthor(author);   // Встановлюємо автора
        // createdAt буде встановлено автоматично через @PrePersist у моделі

        // 4. Зберігаємо коментар у базі даних
        return commentRepository.save(comment);
    }

    // Метод для отримання всіх коментарів до певного продукту
    public List<Comment> getCommentsByProductId(Long productId) {
        return commentRepository.findByProductId(productId);
    }


    // *** НОВИЙ МЕТОД: Оновити коментар з перевіркою автора ***
    @Transactional
    public Comment updateComment(Long commentId, CommentUpdateDto updatedCommentDetails, Long currentUserId) {
        // 1. Знаходимо існуючий коментар
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Коментар з ID '" + commentId + "' не знайдено для оновлення"));

        // 2. Перевірка авторизації: Чи є поточний користувач автором коментаря?
        // АБО (якщо потрібно) перевірка, чи поточний користувач є адміном.
        if (!existingComment.getAuthor().getId().equals(currentUserId)) {
            // Якщо ID автора коментаря не співпадає з ID поточного користувача,
            // кидаємо виняток (наприклад, AccessDeniedException або інший кастомний)
            // Тут ми використовуємо RuntimeException як простий приклад,
            // але в реальному додатку краще мати специфічний виняток авторизації.
            throw new RuntimeException("Користувач не має прав для редагування цього коментаря."); // Або кинь AccessDeniedException
        }

        // 3. Оновлюємо тільки текст коментаря з DTO
        existingComment.setText(updatedCommentDetails.getText());

        // Ми НЕ оновлюємо: ID, автора, продукт, дату створення!

        // 4. Зберігаємо оновлений коментар
        // Spring Data JPA збереже зміни в рамках транзакції
        return commentRepository.save(existingComment);
    }
    // Метод для видалення коментаря (за ID коментаря)
    @Transactional
    public boolean deleteCommentById(Long commentId,Long currentUserId) {
        // 1. Знаходимо продукт, який потрібно видалити
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт з ID '" + commentId + "' не знайдено для видалення"));

        // 2. *** ДОДАНО/ЗМІНЕНО: Перевірка авторизації ***
        try {
            User currentUser = userService.getUserById(currentUserId);

            // Перевіряємо, чи користувач є АДМІНОМ
            boolean isAdmin = currentUser.getRoles() != null && currentUser.getRoles().equals("ADMIN"); // Можливо, проблема з цим порівнянням

            // Якщо користувач НЕ є адміном, виконуємо додаткові перевірки
            if (!isAdmin) {
                // *** ВАЖЛИВО: Перевіряємо, чи у продукту ВЗАГАЛІ є творець перед викликом getCreator().getId() ***
                if (comment.getAuthor() != null) { // <-- ЦЯ ПЕРЕВІРКА МАЄ БУТИ
                    // Якщо творець є, перевіряємо, чи поточний користувач НЕ є цим творцем
                    if (!comment.getAuthor().getId().equals(currentUserId)) { // <-- Цей рядок або наступний може бути рядком 46
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