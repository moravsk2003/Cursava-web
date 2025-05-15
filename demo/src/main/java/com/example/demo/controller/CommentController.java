package com.example.demo.controller;

import com.example.demo.model.Comment;
import com.example.demo.model.User;
import com.example.demo.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.demo.dto.CommentRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.demo.service.UserService; // Потрібен для отримання користувача за UserDetails
import com.example.demo.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/comments") // Базовий шлях для ендпоінтів коментарів
public class CommentController {

    private final CommentService commentService;
    private final UserService userService; // Потрібен для отримання поточного користувача

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }
    // Ендпоінт для додавання нового коментаря до продукту
    // Очікуємо отримати об'єкт з текстом коментаря, ID продукту та ID автора
    // ID автора брати з контексту аутентифікації Spring Security
    @PostMapping("/product/{productId}") // Шлях для додавання коментаря до конкретного продукту
    public ResponseEntity<Comment> addCommentToProduct( @PathVariable Long productId,
                                                        @RequestBody CommentRequest commentRequest // Приймаємо CommentRequest (з текстом)
                                                        // !!! authorId БЕРЕМО З КОНТЕКСТУ АУТЕНТИФІКАЦІЇ !!!
    ) {
        // 1. Отримуємо інформацію про поточного аутентифікованого користувача
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Перевіряємо, чи користувач аутентифікований (цей ендпоінт має бути захищеним)
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401 Unauthorized
        }

        // Отримуємо UserDetails аутентифікованого користувача
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Несподіваний тип Principal
        }
        String currentUsername = ((UserDetails) principal).getUsername(); // Отримуємо логін (email)

        try {
            // 2. Знаходимо повний об'єкт User для поточного користувача за його логіном (email)
            User currentUser = userService.getUserByEmail(currentUsername);
            Long authorId = currentUser.getId(); // <<< Отримуємо ID автора з аутентифікованого користувача!

            // 3. Викликаємо сервісний метод для створення коментаря, передаючи всі потрібні дані
            Comment newComment = commentService.createComment(
                    productId,
                    commentRequest.getText(), // Беремо текст з CommentRequest
                    authorId // <<< Передаємо ID автора
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
        } catch (ResourceNotFoundException e) {
            // Обробка помилки, якщо продукт або користувач (хоча користувач тут має бути знайдений) не існує
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 Not Found
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }

    // Ендпоінт для отримання всіх коментарів до певного продукту
    @GetMapping("/product/get/{productId}") // Шлях для отримання коментарів до конкретного продукту
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
    // Цей ендпоінт, , має бути захищений (видалити може тільки автор або адмін)
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            // 1. Отримуємо інформацію про поточного аутентифікованого користувача
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // Перевіряємо, чи користувач аутентифікований (цей ендпоінт має бути захищеним)
            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401 Unauthorized
            }

            // Отримуємо UserDetails аутентифікованого користувача
            Object principal = authentication.getPrincipal();
            if (!(principal instanceof UserDetails)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Несподіваний тип Principal
            }
            String currentUsername = ((UserDetails) principal).getUsername(); // Отримуємо логін (email)
            // 2. Знаходимо повний об'єкт User для поточного користувача за його логіном (email)
            User currentUser = userService.getUserByEmail(currentUsername);
            Long authorId = currentUser.getId();
            boolean deleted = commentService.deleteCommentById(commentId,authorId); // Використовуємо простий метод з сервісу
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
}
