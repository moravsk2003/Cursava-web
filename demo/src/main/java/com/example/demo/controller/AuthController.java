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
import org.springframework.security.authentication.AuthenticationManager; // Менеджер аутентифікації
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Для створення токена аутентифікації
import org.springframework.security.core.Authentication; // Результат аутентифікації
import org.springframework.security.core.AuthenticationException; // Винятки аутентифікації
import com.example.demo.util.JwtUtil;

import java.util.Map;

@RestController
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
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Користувача успішно зареєстровано!"));

        } catch (RuntimeException e) { //специфічні винятки, наприклад UserAlreadyExistsException
            e.printStackTrace();
            // Якщо користувач з таким email вже існує, сервіс кидає виняток.
            // Можна обробити тут специфічний виняток UserAlreadyExistsException
            // і повернути 409 Conflict.
            // Зараз просто повертаємо 400 Bad Request або 500 Internal Server Error.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Помилка реєстрації: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Неочікувана помилка реєстрації"));
        }
    }

    // Ендпоінт для аутентифікації (логіну) користувача
    @PostMapping("/login") // POST /auth/login
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        try {
            System.out.println("Attempting login with email: " + authRequest.getEmail());
            // 1. Створюємо об'єкт токена аутентифікації на основі логіна та пароля з запиту
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
            // 2. Якщо аутентифікація успішна, отримуємо UserDetails аутентифікованого користувача
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            //Генеруємо JWT токен
            String jwtToken = jwtUtil.generateToken(userDetails);
            // 3. Повертаємо відповідь, що містить згенерований токен
            AuthResponse authResponse = new AuthResponse(jwtToken);
            return ResponseEntity.ok(authResponse); // Повертаємо 200 OK з тілом AuthResponse
        } catch (AuthenticationException e) {
            // Обробка помилок аутентифікації (невірний логін/пароль)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невірний логін або пароль"); // 401 Unauthorized
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неочікувана помилка аутентифікації"); // 500
        }
    }
}