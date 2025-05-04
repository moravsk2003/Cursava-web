package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Генерує гетери, сетери, toString, equals, hashCode
@NoArgsConstructor // Конструктор без аргументів
@AllArgsConstructor // Конструктор з усіма аргументами
public class UserProfileDto {

    private String name;
    private String email;

    // Можна додати інші поля, які безпечно показувати (наприклад, вік, якщо потрібно)
    // private int age;
}
