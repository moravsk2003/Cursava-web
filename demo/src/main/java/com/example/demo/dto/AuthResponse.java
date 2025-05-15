package com.example.demo.dto;

import lombok.Data; // Lombok
import lombok.NoArgsConstructor; // Lombok
import lombok.AllArgsConstructor; // Lombok

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;

}
