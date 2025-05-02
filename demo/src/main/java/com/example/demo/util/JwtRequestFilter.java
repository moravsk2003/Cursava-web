package com.example.demo.util;

import com.example.demo.service.UserService; // Імпортуємо наш UserService (UserDetailsService)
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain; // Імпортуємо Chain
import jakarta.servlet.ServletException; // Імпортуємо Exception
import jakarta.servlet.http.HttpServletRequest; // Імпортуємо Request
import jakarta.servlet.http.HttpServletResponse; // Імпортуємо Response
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Імпортуємо Token
import org.springframework.security.core.context.SecurityContextHolder; // Імпортуємо SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails; // Імпортуємо UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // Імпортуємо DetailsSource
import org.springframework.stereotype.Component; // Імпортуємо Component
import org.springframework.web.filter.OncePerRequestFilter; // Імпортуємо базовий фільтр
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException; // Імпортуємо IOException
@Component // Робить цей клас Spring-компонентом
public class JwtRequestFilter extends OncePerRequestFilter { // Успадковуємося від OncePerRequestFilter

    private final JwtUtil jwtUtil;
    private final UserService userService; // Наш UserDetailsService

    // Конструктор для впровадження залежностей
    public JwtRequestFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    // Метод, який виконується один раз для кожного запиту
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Отримуємо заголовок Authorization
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 2. Перевіряємо, чи заголовок Authorization існує і починається з "Bearer "
        // Токен зазвичай надсилається у форматі: "Authorization: Bearer <токен>"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Витягуємо сам токен (частина після "Bearer ")
            jwt = authorizationHeader.substring(7);
            try {
                // Витягуємо логін користувача (email) з токена
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                // Обробка випадку, коли токен закінчився
                // Можна додати логування або повернути специфічну відповідь 401/403
                logger.warn("JWT token is expired", e);
                // Не кидаємо виняток тут, просто не встановлюємо контекст безпеки.
                // Наступні фільтри (авторизація) відхилять запит до захищених ресурсів.
            } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
                // Обробка інших помилок валідації токена (невірний підпис, некоректний формат тощо)
                logger.error("JWT token validation error", e);
                // Так само, не кидаємо виняток, дозволяємо Spring Security відхилити запит.
            }
        }

        // 3. Якщо логін знайдено в токені І користувач ще не аутентифікований (контекст порожній)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Завантажуємо деталі користувача за логіном з токена
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            // 4. Валідуємо токен (перевіряємо підпис та строк дії)
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // 5. Якщо токен валідний, створюємо об'єкт аутентифікації для Spring Security
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                // Встановлюємо додаткові деталі аутентифікації (IP-адреса, ідентифікатор сесії тощо)
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Встановлюємо об'єкт аутентифікації в контекст безпеки Spring Security
                // Це означає, що поточний користувач вважається аутентифікованим для цього запиту.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        // 7. Передаємо запит далі по ланцюжку фільтрів Spring Security
        filterChain.doFilter(request, response);
    }
}
