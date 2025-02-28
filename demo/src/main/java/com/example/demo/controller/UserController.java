package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @PostMapping("/save")
    public User createUser(@RequestBody User user){
        List<User> users = userService.getAllUsers();
        System.out.println(users);  // Додайте це для перевірки
        //System.out.println("User after save: " + user);
        return userService.createUser(user);

    }

}
