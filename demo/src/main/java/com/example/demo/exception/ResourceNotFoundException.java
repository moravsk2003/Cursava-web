package com.example.demo.exception; // Рекомендовано створити окремий пакет для винятків

public class ResourceNotFoundException extends RuntimeException {

    /**
     * Конструктор для ResourceNotFoundException.
     *
     * @param message Зрозуміле повідомлення про те, який ресурс не знайдено.
     */
    public ResourceNotFoundException(String message) {
        // Викликаємо конструктор батьківського класу RuntimeException
        super(message);
    }

    // Можна додати інші конструктори, якщо потрібно,
    // наприклад, з причиною (Throwable cause)
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}