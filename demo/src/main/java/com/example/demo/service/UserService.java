package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.example.demo.exception.ResourceNotFoundException;
//@AllArgsConstructor
//@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
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
    }
    public User getUserByEmail(String email ){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Користувача з Email '" + email + "' не знайдено"));
    }
    public User getUserByPhoneNumber(String phoneNumber ){
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Користувача з номером телефону '" + phoneNumber + "' не знайдено"));
    }
    public User createUser(User user){
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            // Можна кинути новий виняток UserAlreadyExistsException
            throw new RuntimeException("Користувач з таким Email вже існує"); // Тимчасово, до створення UserAlreadyExistsException
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //user.setRoles("user");
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
    @Override // Вказує, що цей метод перевизначає метод з батьківського інтерфейсу
    @Transactional // Завантаження даних користувача також може бути транзакційним
    // loadUserByUsername використовується Spring Security для завантаження даних користувача за логіном
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Логіном є email користувача
        User user = userRepository.findByEmail(username)
                // Якщо користувача не знайдено, кидаємо стандартний для Spring Security виняток
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Користувача '%s' не знайдено", username)));

        // Повертаємо об'єкт Spring Security UserDetails
        // Цей об'єкт містить логін, пароль та список повноважень (ролей) користувача.
        // Тобі потрібно буде отримати ролі користувача з твоєї моделі User,
        // якщо ти їх зберігаєш. Якщо ні, можна задати роль за замовчуванням, як тут.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Логін (email)
                user.getPassword(), // Пароль (хешований)
                // Список повноважень (ролей) користувача
                // Для простоти, припустимо, що всі користувачі мають роль "ROLE_USER".
                // У реальному проекті ти будеш завантажувати ролі з моделі User.
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // Приклад з фіксованою роллю
        );
    }

}
