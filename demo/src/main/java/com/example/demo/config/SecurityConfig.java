package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity // Вмикаємо конфігурацію веб-безпеки Spring Security
public class SecurityConfig {

    // Бін PasswordEncoder, який ми додавали раніше
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Бін SecurityFilterChain, який налаштовує правила доступу
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Відключаємо CSRF (зазвичай потрібно для REST API без використання сесій на стороні сервера)
                .csrf(csrf -> csrf.disable())
                // 2. Налаштовуємо правила авторизації для HTTP запитів
                .authorizeHttpRequests(auth -> auth
                        // Дозволяємо доступ до певних URL без аутентифікації (публічні ендпоінти)
                        .requestMatchers(
                                "/users/save",       // Дозволити реєстрацію
                                "/users/login",      // Дозволити логін
                                "/users/hel",        // Дозволити /users/hel
                                "/users/text"        // Дозволити /users/text (хоча ми його обговорювали змінити)
                                // Додай тут інші публічні шляхи, якщо є
                        ).permitAll() // Дозволити всім
                        // Для всіх інших запитів вимагаємо аутентифікацію
                        .anyRequest().authenticated()
                );
        // Можеш додати інші налаштування тут, наприклад:
        // .sessionManagement(...) // Налаштування управління сесіями (наприклад, без стану для JWT)
        // .exceptionHandling(...) // Налаштування обробки винятків аутентифікації/авторизації
        // .httpBasic(...) або .formLogin(...) // Налаштування типу аутентифікації (якщо використовуєш не токени)


        return http.build();
    }
}