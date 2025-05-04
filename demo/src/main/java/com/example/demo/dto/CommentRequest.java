package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Гетери, сетери, toString
@NoArgsConstructor // Конструктор без аргументів
@AllArgsConstructor // Конструктор з усіма аргументами
public class CommentRequest {

    @NotBlank(message = "Текст коментаря не може бути пустим")
    private String text;

    // !!! ПОЛЕ authorId ВИДАЛЕНО !!! Воно не повинно прийматися з фронтенду.
    // Автор визначається на бекенді за аутентифікованим користувачем.
}