package com.example.demo.controller;

import com.example.demo.dto.RegistrationRequest; // Імпортуємо наш DTO для реєстрації
import com.example.demo.dto.AuthRequest; // Імпортуємо наш DTO для запиту аутентифікації (логіну)
import com.example.demo.dto.AuthResponse; // Імпортуємо наш DTO для відповіді аутентифікації
import com.example.demo.model.User; // Імпортуємо User модель
import com.example.demo.service.UserService; // Імпортуємо наш UserService

import jakarta.validation.Valid; // Для валідації DTO
import org.springframework.http.HttpStatus; // Для статус кодів
import org.springframework.http.ResponseEntity; // Для повернення ResponseEntity
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// *** ДОДАНО: Імпорти Spring Security для аутентифікації ***
import org.springframework.security.authentication.AuthenticationManager; // Менеджер аутентифікації
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Для створення токена аутентифікації
import org.springframework.security.core.Authentication; // Результат аутентифікації
import org.springframework.security.core.AuthenticationException; // Винятки аутентифікації
import com.example.demo.util.JwtUtil;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Дозволяємо запити з фронтенду
@RequestMapping("/auth") // Базовий шлях для аутентифікації
public class AuthController {

    private final UserService userService;
    // *** ДОДАНО: Менеджер аутентифікації ***
    private final AuthenticationManager authenticationManager; // Spring сам впровадить бін AuthenticationManager
    private final JwtUtil jwtUtil;

    // Конструктор для впровадження залежностей
    public AuthController(UserService userService, AuthenticationManager authenticationManager,JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil; // Впроваджуємо JwtUtil
    }

    // Ендпоінт для реєстрації нового користувача
    @PostMapping("/register") // POST /api/auth/register
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        try {
            // Конвертуємо DTO в сутність User (або робимо це в сервісі)
            User userToRegister = registrationRequest.toUser(); // Використовуємо метод конвертації з DTO

            // Викликаємо метод сервісу для створення користувача
            // Сервіс сам хешує пароль і зберігає користувача
            User registeredUser = userService.createUser(userToRegister);

            // Успішна реєстрація
            // Не повертаємо повний об'єкт User з паролем!
            // Можна повернути DTO без пароля або просто повідомлення про успіх.
            return ResponseEntity.status(HttpStatus.CREATED).body("Користувача успішно зареєстровано!");

        } catch (RuntimeException e) { // Можна ловити більш специфічні винятки, наприклад UserAlreadyExistsException
            e.printStackTrace();
            // Якщо користувач з таким email вже існує, сервіс кидає виняток.
            // Можна обробити тут специфічний виняток UserAlreadyExistsException
            // і повернути 409 Conflict.
            // Зараз просто повертаємо 400 Bad Request або 500 Internal Server Error.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Помилка реєстрації: " + e.getMessage()); // 400
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неочікувана помилка реєстрації"); // 500
        }
    }

    // Ендпоінт для аутентифікації (логіну) користувача
    // !!! ЦЕ ПОТРЕБУВАТИМЕ ДОДАТКОВИХ КРОКІВ ДЛЯ JWT ТОКЕНІВ ПІСЛЯ УСПІШНОЇ АУТЕНТИФІКАЦІЇ !!!
    @PostMapping("/login") // POST /api/auth/login
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        try {
            // 1. Створюємо об'єкт токена аутентифікації на основі логіна та пароля з запиту
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
            // 2. Якщо аутентифікація успішна, отримуємо UserDetails аутентифікованого користувача
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // *** ДОДАНО: Генеруємо JWT токен ***
            String jwtToken = jwtUtil.generateToken(userDetails);

            // 3. Повертаємо відповідь, що містить згенерований токен
            AuthResponse authResponse = new AuthResponse(jwtToken);
            return ResponseEntity.ok(authResponse); // Повертаємо 200 OK з тілом AuthResponse

        } catch (AuthenticationException e) {
            // Обробка помилок аутентифікації (невірний логін/пароль)
            // Глобальний обробник може обробити ці винятки більш красиво
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невірний логін або пароль"); // 401 Unauthorized
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неочікувана помилка аутентифікації"); // 500
        }
    }
}