package com.example.demo.controller;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
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
            createUser3();
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
            createUser3();
            System.out.println(user.getName());
            System.out.println(user.getEmail());
            System.out.println(user.getId());
            System.out.println(user.getPassword());
            System.out.println(user.getPhoneNumber());
            System.out.println(user.getAge());
            // Обробка інших можливих помилок
            ok="false";
            return false;
        }

    }
    @GetMapping("/text")
    public String createUser3(){
        System.out.println("test");
        return "test";

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
        createUser3();
        try {

            Optional<User> a = userService.getUserByEmail(loginRequest.getUsernameOrEmail());
            if (a.isPresent()) {
                User user = a.get(); // Отримуємо об'єкт User
                // Тепер працюємо з об'єктом user
                // Наприклад, перевіряємо пароль:
                if (loginRequest.getPassword().equals(user.getPassword())) {
                    ok="false";
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
