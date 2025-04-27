package com.example.demo.service;


import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

//@AllArgsConstructor
//@RequiredArgsConstructor
@Service
public class UserService {
    private  final UserRepository userRepository;

    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }
    public List<User> getAllUsers(){

        return userRepository.findAll();
    }

    public Optional<User> getUserByEmail(String email ){
        return userRepository.findByEmail(email);
    }
    public Optional<User> getUserByPhoneNumber(String phoneNumber ){
        return userRepository.findByPhoneNumber(phoneNumber);
    }
    public User createUser(User user){

        return userRepository.save(user);
    }

}
