package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.ArrayList; // <-- Імпортуємо ArrayList
import java.util.HashSet;   // <-- Імпортуємо HashSet (для Many-to-Many часто використовують Set)
import java.util.List;      // <-- Імпортуємо List
import java.util.Set;       // <-- Імпортуємо Set
@Entity
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "поле пусте")
    private  String name;
    @NotBlank(message = "поле пусте")
    @Email
    private String email;
    @NotBlank(message = "поле пусте")
    //@Size(min = 4, max = 22, message = "Пароль має бути від {4} до {22} символів")
    private String password;
    @Min(0)
    private int age;
    @Pattern(regexp = "\\d{12}",message = "phon number muct be 12 digsts")
    private String phoneNumber;
    private String roles;
    @ManyToMany(fetch = FetchType.LAZY) // Завантажуємо список улюблених продуктів ліниво (коли він потрібен)
    @JoinTable(
            name = "user_favorite_products", // Назва проміжної таблиці
            joinColumns = @JoinColumn(name = "user_id"), // Стовпець у проміжній таблиці, що посилається на цю сутність (User)
            inverseJoinColumns = @JoinColumn(name = "product_id") // Стовпець у проміжній таблиці, що посилається на іншу сутність (Product)
    )
    @JsonIgnore
    // Використовуємо Set, щоб уникнути дублікатів у списку улюблених
    private Set<Product> favoriteProducts = new HashSet<>(); // Ініціалізуємо порожнім Set


    //  Зв'язок Один-до-Багатьох для створених продуктів ***
    // Один користувач може створити багато продуктів
    // 'author' - це назва поля у сутності Product, яке посилається на User
    @OneToMany(mappedBy = "creator", // 'creator' - поле в Product, яке "володіє" зв'язком
            orphanRemoval = true, // Видаляти продукти, якщо їх видаляють зі списку створених у User
            fetch = FetchType.LAZY) // Завантажуємо список створених продуктів ліниво
    @JsonIgnore
    // Використовуємо List, оскільки порядок може бути важливим (або Set, якщо ні)
    private List<Product> createdProducts = new ArrayList<>(); // Ініціалізуємо порожнім List


    // Конструктор, який був у вас (можливо, знадобиться його оновити, якщо ви використовуєте AllArgsConstructor)
    public User(Long id, String name, String email, int age, String phoneNumber,String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.password=password;
        // Не ініціалізуємо нові списки тут, вони ініціалізовані при оголошенні полів
    }
    //  допоміжні методи для зручного керування зв'язками ***
    // Наприклад, для додавання/видалення улюблених продуктів
    public void addFavoriteProduct(Product product) {
        this.favoriteProducts.add(product);
        product.getFavoritedByUsers().add(this); // Оновлюємо зв'язок на стороні Product
    }

    public void removeFavoriteProduct(Product product) {
        this.favoriteProducts.remove(product);
        product.getFavoritedByUsers().remove(this); // Оновлюємо зв'язок на стороні Product
    }

    // Для додавання/видалення створених продуктів
    public void addCreatedProduct(Product product) {
        this.createdProducts.add(product);
        product.setCreator(this); // Встановлюємо цього користувача як творця продукту
    }

    public void removeCreatedProduct(Product product) {
        this.createdProducts.remove(product);
        product.setCreator(null); // Прибираємо творця з продукту
    }
}