package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Генерує гетери, сетери, toString, equals, hashCode
@NoArgsConstructor // Конструктор без аргументів
@AllArgsConstructor // Конструктор з усіма аргументами
public class UserUpdateDto {

    @NotBlank(message = "Ім'я не може бути пустим")
    private String name;

    @Min(0)
    private int age;

    @Pattern(regexp = "\\d{12}", message = "Номер телефону має складатися з 12 цифр")
    private String phoneNumber;

}
