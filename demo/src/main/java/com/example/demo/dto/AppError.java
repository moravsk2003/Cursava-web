package com.example.demo.dto; // Рекомендовано створити пакет для DTO

import lombok.Data; // Для гетерів та сетерів
import lombok.NoArgsConstructor; // Конструктор без аргументів
// Не додаємо @AllArgsConstructor, оскільки будемо використовувати спеціальний конструктор

import java.util.Date; // Можна використовувати java.util.Date або java.time.LocalDateTime

@Data
@NoArgsConstructor
public class AppError {
    // Статус код помилки (наприклад, 404 для ResourceNotFound)
    private int statusCode;

    // Повідомлення про помилку
    private String message;

    // Час виникнення помилки (корисно для логування та відстеження)
    private Date timestamp; // Або приватний LocalDateTime timestamp;

    /**
     * Конструктор для AppError.
     * Автоматично встановлює час створення.
     *
     * @param statusCode Статус код HTTP (наприклад, HttpStatus.NOT_FOUND.value()).
     * @param message Повідомлення про помилку.
     */
    public AppError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = new Date(); // Або LocalDateTime.now();
    }
}