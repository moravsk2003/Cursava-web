package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    String ok="false";


    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/hel")
    public String sayHello(){
        return "Привіт з беку";
    }
    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }


    @PostMapping("/save")
    public boolean createUser2(@RequestBody User user){
        try {Optional<User> a = userService.getUserByEmail(user.getEmail());
            if (!a.isPresent()) {
                if (userService.createUser(user) != null) {
                    ok="true";
                    return true;
                } else {
                    ok="false";
                    return false;
                }
            }else{
                ok="false";
                return false;
            }
        }catch (Exception e) {
            // Обробка інших можливих помилок
            ok="false";
            return false;
        }

    }
    @GetMapping("/text")
    public String createUser3(){
        System.out.println(ok);
        return ok;

    }
    @PostMapping("/save2")
    public User createUser(@RequestBody User user){
        return userService.createUser(user);

    }
    @PostMapping("/email")
    public Optional<User> getUserByEmail(@RequestBody String email){
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
                if (userService.checkPassword(user,loginRequest.getPassword())) {
                    ok="true";
                    System.out.println("є");
                    return true;
                }
            } else {
                ok="false";
                return false;
            }
            return false;

        } catch (Exception e) {
            // Обробка інших можливих помилок
            ok="false";
            return false;
        }
    }
    @PostMapping("/phone")
    public Optional<User> getUserByPhoneNumber(@RequestBody String phoneNumber){
        return userService.getUserByPhoneNumber(phoneNumber);

    }
}
