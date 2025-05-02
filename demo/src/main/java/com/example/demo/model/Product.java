package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); // Ініціалізуємо список

}
