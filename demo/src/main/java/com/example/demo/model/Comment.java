package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime; // Для збереження дати і часу створення коментаря

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"author", "product"}) // Виключаємо зв'язки для equals/hashCode
@ToString(exclude = {"author", "product"}) // Виключаємо зв'язки для toString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Вказує, що ID буде генеруватися базою даних (автоінкремент)
    private Long id;
    @Column(nullable = false, columnDefinition = "TEXT") // Поле для тексту коментаря. nullable = false означає, що поле не може бути порожнім у базі. columnDefinition = "TEXT" може бути корисним для довших текстів.
    @NotBlank(message = "Текст коментаря не може бути пустим")
    private String text;

    // Зв'язок Many-to-One з User (автор коментаря)
    @ManyToOne(fetch = FetchType.EAGER) // Можливо, захочеш завантажувати автора одразу
    @JoinColumn(name = "user_id", nullable = false)
    private User author; // Посилання на сутність User, який є автором коментаря

    // ЗВ'ЯЗОК: Many-to-One з Product (продукт, що коментується)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) // Зазвичай продукти не потрібно завантажувати одразу з коментарем
    @JoinColumn(name = "product_id", nullable = false) // Стовпець у таблиці comments, що посилається на продукт
    private Product product; // Посилання на сутність Product, до якого відноситься коментар

    @Column(name = "created_at", nullable = false) // Поле для збереження дати та часу створення коментаря
    private LocalDateTime createdAt;

    //  Метод для автоматичного встановлення часу створення перед збереженням
    @PrePersist
     protected void onCreate() {
         createdAt = LocalDateTime.now();
     }
}
