package com.example.demo.repository;

import com.example.demo.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Додаємо імпорт List
import java.util.Optional;

@Repository // Позначає, що це репозиторій
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Додамо метод для пошуку всіх коментарів до певного продукту
    // Spring Data JPA автоматично створить запит за назвою цього методу
    List<Comment> findByProductId(Long productId);

    // Можливо, також знадобиться знайти коментарі за автором
    List<Comment> findByAuthorId(Long authorId);

    // Або знайти конкретний коментар за ID продукту та ID коментаря (якщо потрібно)
    Optional<Comment> findByIdAndProductId(Long id, Long productId);

    // Додай тут інші методи пошуку, якщо вони потрібні
}
