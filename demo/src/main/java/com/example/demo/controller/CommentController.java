package com.example.demo.controller;

import com.example.demo.model.Comment;
import com.example.demo.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Дозволяємо запити з фронтенду на localhost:3000
@RequestMapping("/api/comments") // Базовий шлях для ендпоінтів коментарів
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Ендпоінт для додавання нового коментаря до продукту
    // Очікуємо отримати об'єкт з текстом коментаря, ID продукту та ID автора
    // У реальності, ID автора краще брати з контексту аутентифікації Spring Security
    @PostMapping("/product/{productId}") // Шлях для додавання коментаря до конкретного продукту
    public ResponseEntity<Comment> addCommentToProduct(
            @PathVariable Long productId,
            @RequestBody CommentRequest commentRequest // Використовуємо DTO для вхідних даних
            // У реальному додатку, ти, ймовірно, отримуватимеш Principal або Authentication
            // для отримання інформації про поточного користувача (автора)
            // @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            // Припускаємо, що commentRequest містить поле text та, можливо, authorId
            // У безпечному додатку, authorId потрібно брати з аутентифікованого користувача
            // Long authorId = ((User) ((UsernamePasswordAuthenticationToken) authentication).getPrincipal()).getId(); // Приклад, якщо Principal є User
            // Або з UserDetails, якщо ти використовуєш кастомний UserDetails

            // Зараз просто використовуємо authorId з запиту (НЕБЕЗПЕЧНО В РЕАЛЬНОМУ ДОДАТКУ)
            Long authorId = commentRequest.getAuthorId(); // Потрібно отримати authorId з запиту або контексту

            Comment newComment = commentService.createComment(
                    productId,
                    commentRequest.getText(),
                    authorId // Передаємо ID автора
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(newComment); // Повертаємо 201 Created і створений коментар
        } catch (RuntimeException e) {
            // Обробка помилок, наприклад, продукт або користувач не знайдено
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Або інший статус, наприклад, 400 Bad Request
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }

    // Ендпоінт для отримання всіх коментарів до певного продукту
    @GetMapping("/product/{productId}") // Шлях для отримання коментарів до конкретного продукту
    public ResponseEntity<List<Comment>> getCommentsForProduct(@PathVariable Long productId) {
        try {
            List<Comment> comments = commentService.getCommentsByProductId(productId);
            return ResponseEntity.ok(comments); // Повертаємо 200 OK і список коментарів
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }

    // Ендпоінт для видалення коментаря за його ID
    @DeleteMapping("/{commentId}") // Шлях для видалення коментаря за його ID
    // Цей ендпоінт, ймовірно, має бути захищений (видалити може тільки автор або адмін)
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            // У реальному додатку, перевіряй, чи поточний користувач має право видаляти цей коментар
            // Long currentUserId = ... отримати з контексту аутентифікації
            // boolean deleted = commentService.deleteCommentByIdIfAuthor(commentId, currentUserId); // Використовуй такий метод

            boolean deleted = commentService.deleteCommentById(commentId); // Використовуємо простий метод з сервісу

            if (deleted) {
                return ResponseEntity.noContent().build(); // 204 No Content при успішному видаленні
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found"); // 404 Not Found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the comment"); // 500
        }
    }


    // ----- DTO для вхідних даних коментаря -----
    // Створи окремий клас CommentRequest у пакеті com.example.demo.model.dto або схожому
    // Наприклад:
    static class CommentRequest {
        private String text;
        private Long authorId; // У реальному додатку це поле не передається з фронтенду, а береться з контексту безпеки

        // Гетери та сетери (Lombok @Data або вручну)
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public Long getAuthorId() { return authorId; }
        public void setAuthorId(Long authorId) { this.authorId = authorId; }
    }
}
