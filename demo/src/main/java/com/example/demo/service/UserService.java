package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import com.example.demo.exception.ResourceNotFoundException;
//@AllArgsConstructor
//@RequiredArgsConstructor
@Service
public class UserService {
    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    //getUserById тепер кидає виняток, якщо користувача не знайдено
    public User getUserById(Long id){ // Змінив тип повернення з Optional<User> на User
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Користувача з ID '" + id + "' не знайдено"));
    }    public Optional<User> getUserByEmail(String email ){
        return userRepository.findByEmail(email);
    }
    public Optional<User> getUserByPhoneNumber(String phoneNumber ){
        return userRepository.findByPhoneNumber(phoneNumber);
    }
    public User createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public boolean checkPassword(User user, String rawPassword) {
        // Використовуємо matches() для порівняння введеного (сирого) пароля з хешованим
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
    @Transactional // Видалення одного користувача також може бути частиною транзакції
    public boolean deleteUserById(Long userId) {
        // Перевіряємо, чи існує користувач з таким ID
        if (userRepository.existsById(userId)) {
            // Якщо існує, видаляємо його
            userRepository.deleteById(userId);
            // Можна додатково перевірити, чи дійсно він видалився, але deleteById
            // зазвичай працює як очікується, або кидає виняток при проблемах.
            // Для простоти, припустимо, що якщо existsById було true, і deleteById
            // не кинув виняток, то видалення було успішним.
            return true; // Користувача знайдено і видалено
        } else {
            return false; // Користувача з таким ID не знайдено
        }
    }

}
