package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/users")
public class UserController {
    private final UserService userService;


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
    public User createUser(@RequestBody User user){
        return userService.createUser(user);

    }
    @PostMapping("/email")
    public Optional<User> getUserByEmail(@RequestBody String email){
        return userService.getUserByEmail(email);

    }
    @PostMapping("/phone")
    public Optional<User> getUserByPhoneNumber(@RequestBody String phoneNumber){
        return userService.getUserByPhoneNumber(phoneNumber);

    }
}
