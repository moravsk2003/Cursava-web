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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

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
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Дозволяємо запити з localhost:3000
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        // Дозволяємо потрібні HTTP методи
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Дозволяємо потрібні заголовки (Authorization, Content-Type, Accept тощо)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        // Дозволяємо надсилати куки та дані для аутентифікації (наприклад, у випадку сесій або Basic Auth, хоча для JWT менш типово)
        configuration.setAllowCredentials(true);
        // Максимальний час, протягом якого результати preflight запиту можуть кешуватися (в секундах)
        configuration.setMaxAge(3600L);

        // Застосовуємо конфігурацію CORS до всіх шляхів (/**)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Налаштовуємо провайдер аутентифікації, який використовує наш UserDetailsService та PasswordEncoder
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService); // Вказуємо наш сервіс для завантаження користувачів
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder); // Вказуємо наш хешер паролів
        return daoAuthenticationProvider;
    }

    // Бін SecurityFilterChain, який налаштовує правила доступу(МОДИФІКОВАНО для додавання JWT фільтра)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception { // Впроваджуємо JwtRequestFilter як аргумент
        http

                // Відключаємо CSRF
                .csrf(AbstractHttpConfigurer::disable)
// *** ДОДАНО: Застосовуємо глобальну конфігурацію CORS ***
                // Важливо, щоб CORS був застосований рано в ланцюжку фільтрів.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Налаштовуємо правила авторизації
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Ендпоінти аутентифікації публічні
                        // Інші публічні ендпоінти (якщо є)
                        .requestMatchers("/products/by-type","/users/hel", "/product/hel","/auth/register","/users","/products","comments/product/get/{productId}").permitAll()
                        // Налаштуйте публічні GET ендпоінти продуктів/коментарів, якщо вони доступні без аутентифікації
                        // .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll() // Приклад: всі GET запити до /api/products
                        // .requestMatchers(HttpMethod.GET, "/api/comments/product/**").permitAll() // Приклад: всі GET запити до коментарів продукту
                        // Всі інші запити вимагають аутентифікації
                        .anyRequest().authenticated()
                )
                // Налаштовуємо безстатусні сесії
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Додаємо JWT фільтр до ланцюжка фільтрів ***
                // Розміщуємо його перед стандартним фільтром UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

        // 4. Можна додати інші налаштування, наприклад, обробку винятків аутентифікації/авторизації
        // .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint)) // Якщо потрібен кастомний обробник неавторизованого доступу

        ; // Закінчення конфігурації http

        return http.build();
    }






}