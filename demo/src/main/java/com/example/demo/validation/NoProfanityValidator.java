package com.example.demo.validation;

import com.example.demo.validation.NoProfanity; // Шлях до вашої анотації
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// Позначаємо як компонент Spring, якщо ProfanityChecker також є компонентом
@Component
public class NoProfanityValidator /*implements ConstraintValidator<NoProfanity, String>*/ {

    //private final ProfanityChecker profanityChecker; // Перевикористовуємо логіку з попереднього прикладу

  //  @Autowired // Впроваджуємо ProfanityChecker
//    public NoProfanityValidator(ProfanityChecker profanityChecker) {
//        this.profanityChecker = profanityChecker;
    //}
//
    //@Override/
    //public void initialize(NoProfanity constraintAnnotation) {
//        // Тут можна отримати параметри з анотації, якщо вони є
    //}
//
    //@Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
       // null вважається валідним (якщо поле не @NotNull). Додайте @NotNull, якщо поле обов'язкове.
        if (value == null) {
            return true;
        }
        return false;
//        // Викликаємо нашу логіку перевірки
//        return !profanityChecker.containsProfanity(value); // Повертаємо true, якщо НЕ містить заборонених слів
    }
}
