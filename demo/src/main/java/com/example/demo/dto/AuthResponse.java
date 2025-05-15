package com.example.demo.dto;

import lombok.Data; // Lombok
import lombok.NoArgsConstructor; // Lombok
import lombok.AllArgsConstructor; // Lombok

@Data // Гетери, сетери, toString, equals, hashCode
@NoArgsConstructor // Конструктор без аргументів
@AllArgsConstructor // Конструктор з усіма аргументами
public class AuthResponse {
    private String token;

}
