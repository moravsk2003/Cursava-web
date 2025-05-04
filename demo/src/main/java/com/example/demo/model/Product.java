package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="products")
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "поле Original Title не може бути пустим")
    private String originalTitle;
    @Enumerated(EnumType.STRING) // Вказує JPA зберігати Enum як рядок в базі даних (назву константи)
    @NotNull(message = "Поле Тип не може бути пустим")
    private ProductType type; // Тип поля змінено на ProductType
    @NotBlank(message = "поле пусте")
    private String description;
    @Min(0)
    private int reviewCount;
    private int averageRating;
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); // Ініціалізуємо список
    //  Зв'язок Багато-до-Одним для творця продукту ***
    // Багато продуктів можуть мати одного творця (User)

    @ManyToOne(fetch = FetchType.LAZY) // Завантажуємо творця ліниво
    @JoinColumn(name = "creator_id") // Назва стовпця у таблиці products, що буде зовнішнім ключем до таблиці users
    private User creator; // Поле, що посилається на користувача, який створив продукт

    // Зв'язок Багато-до-Багатьох для користувачів, що додали в обране ***
    // Багато продуктів можуть бути улюбленими для багатьох користувачів
    // 'favoriteProducts' - це назва поля у сутності User, яке "володіє" цим зв'язком
    @ManyToMany(mappedBy = "favoriteProducts", fetch = FetchType.LAZY) // 'mappedBy' вказує, що зв'язком керує інша сторона (User)
    // Використовуємо Set, щоб уникнути дублікатів у списку
    @JsonIgnore
    private Set<User> favoritedByUsers = new HashSet<>(); // Список користувачів, які додали цей продукт в обране


    // Конструктор, який був у вас (можливо, знадобиться його оновити, якщо ви використовуєте AllArgsConstructor)
    public Product(Long id, String originalTitle, ProductType type, String description, int reviewCount, int averageRating, List<Comment> comments) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.type = type;
        this.description = description;
        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
        this.comments = comments;
        // Не ініціалізуємо нові поля creator та favoritedByUsers тут
    }
    // допоміжні методи для зручного керування зв'язками ***
    // Наприклад, для додавання/видалення коментарів (якщо потрібно керувати з боку продукту)
    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setProduct(this); // Встановлюємо цей продукт у коментарі
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setProduct(null); // Прибираємо продукт з коментаря
    }

}
