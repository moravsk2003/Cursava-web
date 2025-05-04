package com.example.demo.exception; // Можна розмістити тут, або в окремому пакеті handlers

import com.example.demo.dto.AppError; // Імпортуємо наш клас для тіла помилки
import org.springframework.http.HttpStatus; // Імпортуємо HttpStatus для статус кодів
import org.springframework.http.ResponseEntity; // Імпортуємо ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice; // Імпортуємо @ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler; // Імпортуємо @ExceptionHandler
import lombok.extern.slf4j.Slf4j; // Для логування (потребує Lombok)

/**
 * Глобальний обробник винятків для REST контролерів.
 * Перехоплює певні типи винятків та повертає стандартизовані відповіді про помилки.
 */
@ControllerAdvice // Анотація, що позначає цей клас як глобальний обробник винятків для контролерів
@Slf4j // Анотація Lombok для автоматичного створення логера (slf4j)
public class GlobalExceptionHandler {

    /**
     * Обробляє винятки типу ResourceNotFoundException.
     *
     * @param e Виняток ResourceNotFoundException, що виник.
     * @return ResponseEntity зі статусом 404 NOT FOUND та тілом AppError.
     */
    @ExceptionHandler // Анотація, що позначає метод як обробник винятків
    public ResponseEntity<AppError> catchResourceNotFoundException(ResourceNotFoundException e) {
        // Логуємо помилку на сервері (для налагодження)
        log.error(e.getMessage(), e);

        // Створюємо об'єкт AppError з потрібним статус кодом та повідомленням
        AppError error = new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage());

        // Повертаємо ResponseEntity зі статусом 404 NOT FOUND та нашим тілом помилки AppError
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Тут можна додати інші методи @ExceptionHandler для обробки інших типів винятків,
    // наприклад, @ExceptionHandler(MethodArgumentNotValidException.class) для помилок валідації
    // або @ExceptionHandler(Exception.class) для загальних неочікуваних помилок.
}
