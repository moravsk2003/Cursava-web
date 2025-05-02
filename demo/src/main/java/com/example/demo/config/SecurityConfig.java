package com.example.demo.config;

import com.example.demo.service.UserService; // Імпортуємо наш UserService (який тепер UserDetailsService)
import com.example.demo.util.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager; // Імпортуємо AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Імпортуємо провайдер аутентифікації
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Імпортуємо для отримання AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Для відключення CSRF
import org.springframework.security.config.http.SessionCreationPolicy; // Для налаштування сесій
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity // Вмикаємо конфігурацію веб-безпеки Spring Security
public class SecurityConfig {

    // Бін PasswordEncoder, який ми додавали раніше
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Налаштовуємо провайдер аутентифікації, який використовує наш UserDetailsService та PasswordEncoder
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService); // Вказуємо наш сервіс для завантаження користувачів
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder); // Вказуємо наш хешер паролів
        return daoAuthenticationProvider;
    }

    // Бін SecurityFilterChain, який налаштовує правила доступу
    // Бін SecurityFilterChain (МОДИФІКОВАНО для додавання JWT фільтра)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception { // Впроваджуємо JwtRequestFilter як аргумент
        http
                // Відключаємо CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // Налаштовуємо правила авторизації
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Ендпоінти аутентифікації публічні
                        // Інші публічні ендпоінти (якщо є)
                        .requestMatchers("/users/hel", "/product/hel").permitAll()
                        // Налаштуйте публічні GET ендпоінти продуктів/коментарів, якщо вони доступні без аутентифікації
                        // .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll() // Приклад: всі GET запити до /api/products
                        // .requestMatchers(HttpMethod.GET, "/api/comments/product/**").permitAll() // Приклад: всі GET запити до коментарів продукту
                        // Всі інші запити вимагають аутентифікації
                        .anyRequest().authenticated()
                )
                // Налаштовуємо безстатусні сесії
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // *** ДОДАНО: Додаємо наш JWT фільтр до ланцюжка фільтрів ***
                // Розміщуємо його перед стандартним фільтром UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

        // 4. Можна додати інші налаштування, наприклад, обробку винятків аутентифікації/авторизації
        // .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint)) // Якщо потрібен кастомний обробник неавторизованого доступу

        ; // Закінчення конфігурації http

        return http.build();
    }






}