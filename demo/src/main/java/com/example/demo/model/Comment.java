package com.example.demo.model;
import com.example.demo.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime; // Для збереження дати і часу створення коментаря

@Entity // Вказує, що цей клас є сутністю JPA і буде мапитися на таблицю в базі даних
@Table(name = "comments") // Вказує назву таблиці в базі даних (необов'язково, якщо назва класу = назва таблиці)
@Data // Анотація Lombok для автоматичного створення гетерів, сетерів, toString, equals і hashCode
@NoArgsConstructor // Анотація Lombok для створення конструктора без аргументів
@AllArgsConstructor // Анотація Lombok для створення конструктора з усіма аргументами
public class Comment {

    @Id // Позначає поле як первинний ключ
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
