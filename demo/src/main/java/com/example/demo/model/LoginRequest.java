package com.example.demo.model;

import jakarta.validation.constraints.NotBlank; // Або використовуйте javax.validation.constraints.NotBlank для старіших версій Jakarta EE / Java EE

public class LoginRequest {

    @NotBlank(message = "Логін або Email не може бути порожнім")
    private String usernameOrEmail; // Поле для введення логіна або email

    @NotBlank(message = "Пароль не може бути порожнім")
    private String password; // Поле для введення пароля

    // --- Геттери та Сеттери ---
    // Вони потрібні, щоб Spring міг отримати дані з вхідного JSON/тіла запиту
    // та щоб ваш код міг отримати доступ до цих даних.

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Також корисно мати конструктор за замовчуванням (без аргументів),
    // хоча він часто генерується автоматично, якщо немає інших конструкторів.
    public LoginRequest() {
    }

    // Можливо, конструктор з аргументами для зручності тестування, але не обов'язково для роботи Spring.
    public LoginRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    // Також можна додати метод toString() для легшого логування та налагодження
    @Override
    public String toString() {
        return "LoginRequest{" +
                "usernameOrEmail='" + usernameOrEmail + '\'' +
                // Не слід включати пароль у toString() в реальних додатках через безпеку!
                '}';
    }
}