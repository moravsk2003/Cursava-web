package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// Ваш клас валідатора повинен імплементувати ConstraintValidator
// Перший параметр: тип анотації (@NoProfanity)
// Другий параметр: тип даних, який буде валідуватися (наприклад, String)
public class NoProfanityValidator implements ConstraintValidator<NoProfanity, String> {

    @Override
    public void initialize(NoProfanity constraintAnnotation) {
        // Можна використовувати для отримання параметрів з анотації, якщо вони є
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            // Вирішіть, як поводитись з null (зазвичай true, якщо @NotNull не використовується)
            return true;
        }
        // Тут має бути ваша логіка перевірки на заборонені слова
        // Наприклад:
        // return !value.toLowerCase().contains("заборонене_слово");

        // Замініть це на реальну логіку перевірки
        boolean containsProfanity = checkProfanity(value);

        return !containsProfanity; // Повертаємо true, якщо немає заборонених слів
    }

    // Допоміжний метод для вашої логіки перевірки
    private boolean checkProfanity(String text) {
        // Ваша логіка перевірки тексту на наявність заборонених слів
        // Повертає true, якщо знайдено заборонене слово, інакше false

        // Приклад (замініть на вашу реальну логіку):
        String lowerText = text.toLowerCase();
        if (lowerText.contains("badword1") || lowerText.contains("badword2")) {
            return true;
        }
        return false;
    }
}
