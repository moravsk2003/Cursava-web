package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data; // Lombok
import lombok.NoArgsConstructor; // Lombok
import lombok.AllArgsConstructor; // Lombok
import com.example.demo.model.User; // Імпортуємо User, якщо потрібно для конвертації


@Data // Гетери, сетери, toString, equals, hashCode
@NoArgsConstructor // Конструктор без аргументів
@AllArgsConstructor // Конструктор з усіма аргументами
public class RegistrationRequest {

    @NotBlank(message = "Ім'я не може бути пустим") // Приклад валідації
    private String name;

    @NotBlank(message = "Email не може бути пустим")
    @Email(message = "Некоректний формат Email")
    private String email;
    @Size(min = 4, max = 22, message = "Пароль має бути від {4} до {22} символів")
    @NotBlank(message = "Пароль не може бути пустим")
    // Тут можна додати @Size або @Pattern для валідації пароля
    private String password;
    @Min(0)
    private int age;
    @Pattern(regexp = "\\d{12}",message = "phon number muct be 12 digsts")
    private String phoneNumber;

    // Можна додати метод для конвертації в сутність User
    public User toUser() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password); // Пароль буде хешовано в сервісі
        // Встанови інші поля, якщо вони є в DTO
        user.setAge(this.age);
        user.setPhoneNumber(this.phoneNumber);
        return user;
    }
}
