package com.example.demo.util; // Рекомендовано створити пакет для JWT

import io.jsonwebtoken.*; // Імпорти з бібліотеки jjwt
import io.jsonwebtoken.io.Decoders; // Для декодування секретного ключа
import io.jsonwebtoken.security.Keys; // Для роботи з ключами
import org.springframework.beans.factory.annotation.Value; // Для отримання значень з application.properties
import org.springframework.security.core.userdetails.UserDetails; // Для роботи з деталями користувача
import org.springframework.stereotype.Component; // Позначає клас як компонент Spring

import java.security.Key; // Для представлення ключа
import java.util.Date; // Для роботи з датами
import java.util.HashMap; // Для створення claims
import java.util.Map; // Для роботи з claims
import java.util.function.Function; // Для отримання інформації з токена

/**
 * Утиліта для генерації та валідації JWT токенів.
 */
@Component // Робить цей клас Spring-компонентом, щоб його можна було впроваджувати
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Метод для генерації JWT токена на основі UserDetails
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); // Можна додати додаткові дані (claims) до токена, наприклад, ролі

        // Для простоти, додамо лише логін користувача як суб'єкт (subject) токена.
        // У реальному додатку тут можна додати ID користувача, ролі тощо.
        return createToken(claims, userDetails.getUsername()); // Викликаємо приватний метод для створення токена
    }

    // Приватний метод для створення токена
    private String createToken(Map<String, Object> claims, String subject) {
        // Отримуємо ключ для підпису
        Key signinKey = getSigningKey();

        return Jwts.builder()
                .setClaims(claims) // Додаткові дані (claims)
                .setSubject(subject) // Суб'єкт токена (зазвичай логін або ID користувача)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Час створення токена
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Час закінчення життя токена
                .signWith(signinKey, SignatureAlgorithm.HS256) // Підписуємо токен секретним ключем за алгоритмом HS256
                .compact(); // Будуємо та стискаємо токен у рядок
    }

    // Метод для отримання ключа підпису з секретного рядка
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret); // Декодуємо секретний ключ з Base64
        return Keys.hmacShaKeyFor(keyBytes); // Створюємо HMAC ключ
    }

    // Метод для витягування інформації (claims) з токена
    private Claims extractAllClaims(String token) {
        // Парсимо токен та повертаємо всі claims
        return Jwts.parser()
                .setSigningKey(getSigningKey()) // Вказуємо ключ для валідації підпису
                .build()
                .parseClaimsJws(token) // Парсимо JWS (Signed JWT)
                .getBody(); // Отримуємо тіло токена з claims
    }

    // Метод для витягування конкретного claim з токена
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Отримуємо всі claims
        return claimsResolver.apply(claims); // Застосовуємо функцію для отримання потрібного claim
    }

    // Метод для витягування логіна (суб'єкта) з токена
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // Витягуємо суб'єкт
    }

    // Метод для витягування дати закінчення життя токена
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration); // Витягуємо дату закінчення
    }

    // Метод для перевірки, чи токен закінчився
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // Порівнюємо дату закінчення з поточною датою
    }

    // Метод для валідації токена
    public Boolean validateToken(String token, UserDetails userDetails) {
        // 1. Витягуємо логін з токена
        final String username = extractUsername(token);

        // 2. Перевіряємо, чи логін у токені співпадає з логіном UserDetails
        // та чи токен не закінчився
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


}
