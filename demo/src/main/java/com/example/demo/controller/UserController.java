package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    String ok = "false";


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
            Optional<User> existingUser = userService.getUserByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                ok = "false";
                return false;
            } else {
                if (userService.createUser(user) != null) {
                    ok = "true";
                    return true;
                } else {
                    ok = "false";
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Обробка інших можливих помилок
            ok = "false";
            return false;
        }

    }

    @GetMapping("/text")
    public String createUser3() {
        System.out.println(ok);
        return ok;

    }

    @PostMapping("/save")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);

    }

    @PostMapping("/email")
    public Optional<User> getUserByEmail(@RequestBody String email) {
        return userService.getUserByEmail(email);

    }

    @PostMapping("/login") // Обробляє POST запити на /api/auth/login
    public boolean authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {

            Optional<User> a = userService.getUserByEmail(loginRequest.getEmail());
            if (a.isPresent()) {
                User user = a.get(); // Отримуємо об'єкт User
                // Тепер працюємо з об'єктом user
                // Наприклад, перевіряємо пароль:
                if (userService.checkPassword(user, loginRequest.getPassword())) {
                    ok = "true";
                    System.out.println("є");
                    return true;
                }
            } else {
                ok = "false";
                return false;
            }
            return false;

        } catch (Exception e) {
            // Обробка інших можливих помилок
            ok = "false";
            return false;
        }
    }

    @PostMapping("/phone")
    public Optional<User> getUserByPhoneNumber(@RequestBody String phoneNumber) {
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

}
