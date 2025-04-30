package com.example.demo.service;


import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

//@AllArgsConstructor
//@RequiredArgsConstructor
@Service
public class UserService {
    @Autowired
    private  final UserRepository userRepository;

    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }
    @Transactional // Видалення має виконуватися в транзакції
    public void clearYourTable() {
        // Варіант 1: deleteAll() - може бути повільнішим для великих таблиць, видаляє сутності по черзі
        // yourEntityRepository.deleteAll();

        // Варіант 2: deleteAllInBatch() - зазвичай швидше, виконує один SQL DELETE запит
        userRepository.deleteAllInBatch();

        System.out.println("Таблиця YourEntity очищена.");
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
