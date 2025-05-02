package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank; // Або використовуйте javax.validation.constraints.NotBlank для старіших версій Jakarta EE / Java EE
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Гетери, сетери, toString, equals, hashCode
@NoArgsConstructor // Конструктор без аргументів
@AllArgsConstructor // Конструктор з усіма аргументами
public class AuthRequest {

    @NotBlank(message = "Email не може бути порожнім")
    private String email; // Поле для введення логіна або email

    @NotBlank(message = "Пароль не може бути порожнім")
    private String password; // Поле для введення пароля



    // Також можна додати метод toString() для легшого логування та налагодження
    @Override
    public String toString() {
        return "LoginRequest{" +
                "usernameOrEmail='" + email + '\'' +
                // Не слід включати пароль у toString() в реальних додатках через безпеку!
                '}';
    }
}