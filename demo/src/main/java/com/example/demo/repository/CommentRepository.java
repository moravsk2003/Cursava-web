package com.example.demo.repository;

import com.example.demo.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // Додаємо імпорт List
import java.util.Optional;

@Repository // Позначає, що це репозиторій
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductId(Long productId);
    List<Comment> findByAuthorId(Long authorId);
    Optional<Comment> findByIdAndProductId(Long id, Long productId);
}
