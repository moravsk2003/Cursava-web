package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank; // Для валідації
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Конструктор без аргументів
@AllArgsConstructor // Конструктор з усіма аргументами
public class CommentUpdateDto {

    @NotBlank(message = "Текст коментаря не може бути пустим")
    private String text;

}
