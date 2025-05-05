package com.example.demo.validation;

import com.example.demo.validation.NoProfanityValidator; // Шлях до вашого валідатора
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER}) // Дозволяє застосовувати до полів класів та параметрів методів
@Retention(RetentionPolicy.RUNTIME) // Анотація буде доступна під час виконання
@Constraint(validatedBy = NoProfanityValidator.class) // Вказуємо клас, який буде виконувати валідацію
public @interface NoProfanity {

    String message() default "Текст містить заборонені слова"; // Повідомлення за замовчуванням при помилці

    Class<?>[] groups() default {}; // Стандартні групи валідації

    Class<? extends Payload>[] payload() default {}; // Стандартні пейлоади
}
