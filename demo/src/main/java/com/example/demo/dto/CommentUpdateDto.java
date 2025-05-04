package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank; // Для валідації
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Генерує гетери, сетери, toString, equals, hashCode
@NoArgsConstructor // Конструктор без аргументів
@AllArgsConstructor // Конструктор з усіма аргументами
public class CommentUpdateDto {

    // Єдине поле, яке можна оновлювати в коментарі - це текст
    @NotBlank(message = "Текст коментаря не може бути пустим")
    private String text;

    // Ми не оновлюємо ID коментаря, продукт до якого він належить, автора чи дату створення через цей DTO
}
