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

    // Поля, які користувач може оновлювати через цей ендпоінт
    // Без зміни пароля та ролей! Email зазвичай теж не змінюють тут,
    // або потрібен окремий процес верифікації.
    @NotBlank(message = "Ім'я не може бути пустим")
    private String name;

    @Min(0)
    private int age;

    @Pattern(regexp = "\\d{12}", message = "Номер телефону має складатися з 12 цифр")
    private String phoneNumber;

    // Не включаємо поля password та roles
    // Не включаємо id (воно буде в URL шляху)
    // Не включаємо email (якщо його не можна редагувати через цей ендпоінт)
}
