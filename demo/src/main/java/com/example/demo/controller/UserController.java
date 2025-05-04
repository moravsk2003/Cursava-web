package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/users")

public class UserController {
    private final UserService userService;



    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hel")
    public String sayHello() {
        return "Привіт з беку";
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @PostMapping("/register")
    public boolean createUser2(@RequestBody User user) {
        try {
            User existingUser = userService.getUserByEmail(user.getEmail());

                if (userService.createUser(user) != null) {

                    return true;
                } else {

                    return false;
                }

        } catch (Exception e) {
            e.printStackTrace();
            // Обробка інших можливих помилок

            return false;
        }

    }

    @GetMapping("/text")
    public String createUser3() {
        System.out.println("ok");
        return "ok";

    }

    @PostMapping("/save")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);

    }

    @PostMapping("/email")
    public User getUserByEmail(@RequestBody String email) {
        return userService.getUserByEmail(email);

    }

    @PostMapping("/login") // Обробляє POST запити на /api/auth/login
    public boolean authenticateUser(@Valid @RequestBody AuthRequest loginRequest) {
        try {

            User user = userService.getUserByEmail(loginRequest.getEmail());
                // Наприклад, перевіряємо пароль:
                if (userService.checkPassword(user, loginRequest.getPassword())) {
                    System.out.println("є");
                    return true;
                }
            return false;

        } catch (Exception e) {
            // Обробка інших можливих помилок
            return false;
        }
    }

    @PostMapping("/phone")
    public User getUserByPhoneNumber(@RequestBody String phoneNumber) {
        return userService.getUserByPhoneNumber(phoneNumber);

    }

    @DeleteMapping("/{userId}") // Використовуємо HTTP метод DELETE і передаємо ID в шляху
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            boolean deleted = userService.deleteUserById(userId);

            if (deleted) {
                // Користувача знайдено і видалено
                System.out.println("Користувач з ID " + userId + " успішно видалений.");
                return ResponseEntity.ok().body("User deleted successfully"); // Можна повернути 200 OK
                // Або частіше для успішного видалення повертають 204 No Content:
                // return ResponseEntity.noContent().build();
            } else {
                // Користувача з таким ID не знайдено
                System.out.println("Користувача з ID " + userId + " не знайдено.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"); // 404 Not Found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the user"); // 500
        }
    }
    @GetMapping("/me") // Використовуємо /me для позначення "поточний користувач"
    public ResponseEntity<UserProfileDto> getCurrentUserProfile() {
        // 1. Отримуємо об'єкт аутентифікації з контексту безпеки Spring Security
        // Контекст безпеки зберігає інформацію про поточного аутентифікованого користувача.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Перевіряємо, чи користувач аутентифікований (чи Authentication не null і не анонімний)
        // Хоча цей ендпоінт буде захищено Spring Security, ця перевірка може бути корисною
        // для ясності або в інших сценаріях.
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            // Теоретично, Spring Security має це перехопити раніше і повернути 401 Unauthorized,
            // але ми можемо повернути, наприклад, 404 Not Found, якщо вважаємо, що користувач не знайдений (хоча 401/403 більш правильні)
            // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Або 403 Forbidden
            // У цьому сценарії (захищений ендпоінт) цей блок, ймовірно, ніколи не буде досягнутий.
            // Якщо ви бачите тут помилку, це може вказувати на проблему в конфігурації безпеки.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Приклад повернення 404
        }


        // 2. Отримуємо Principal (зазвичай UserDetails) з об'єкта аутентифікації
        // UserDetails - це об'єкт, який створив ваш UserService.loadUserByUsername()
        Object principal = authentication.getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            // Якщо Principal є UserDetails (стандартний випадок для Spring Security)
            username = ((UserDetails) principal).getUsername(); // Отримуємо логін (email) користувача
        } else {
            // Якщо Principal іншого типу (наприклад, просто рядок імені для анонімних користувачів)
            username = principal.toString();
        }

        // 3. Використовуємо логін (email), щоб знайти повний об'єкт User у базі даних
        try {
            User user = userService.getUserByEmail(username);

            // 4. Створюємо DTO з потрібними даними
            UserProfileDto userProfileDto = new UserProfileDto(user.getName(), user.getEmail());
            // Якщо потрібно додати вік:
            // userProfileDto.setAge(user.getAge());

            // 5. Повертаємо DTO у відповіді з статусом 200 OK
            return ResponseEntity.ok(userProfileDto);

        } catch (ResourceNotFoundException e) {
            // Цей виняток може виникнути, якщо користувача, який був аутентифікований,
            // не знайдено в базі даних (дуже малоймовірний сценарій, якщо система коректна)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Повертаємо 404 Not Found
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }
    // *** НОВИЙ МЕТОД: Оновити користувача за ID (без зміни пароля) ***
    @PutMapping("/{id}") // PUT /users/{id}
    public ResponseEntity<User> updateUser(
            @PathVariable Long id, // ID користувача з URL шляху
            @Valid @RequestBody UserUpdateDto userUpdateDetails // Дані для оновлення з тіла запиту
    ) {
        // *** Важливо: Додати логіку авторизації тут! ***
        // Перевірити, чи ID користувача в шляху ({id}) відповідає ID аутентифікованого користувача,
        // АБО якщо аутентифікований користувач має роль "ADMIN", дозволити оновлення будь-якого користувача.
        // Приклад отримання ID поточного користувача:
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // String authenticatedUserEmail = userDetails.getUsername(); // Це email
        // User authenticatedUser = userService.getUserByEmail(authenticatedUserEmail);
        // if (!id.equals(authenticatedUser.getId()) && !authenticatedUser.getRoles().equals("admin")) {
        //    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403 Forbidden
        // }


        try {
            // Викликаємо сервісний метод для оновлення
            User updatedUser = userService.updateUser(id, userUpdateDetails);
            // *** Можливо, повернути DTO без пароля та інших чутливих даних ***
            return ResponseEntity.ok(updatedUser); // Повертаємо 200 OK та оновленого користувача
        } catch (ResourceNotFoundException e) {
            throw e; // GlobalExceptionHandler обробить і поверне 404
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500
        }
    }

}
