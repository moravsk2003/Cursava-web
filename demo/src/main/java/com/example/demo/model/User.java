package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "поле пусте")
    private  String name;
    @NotBlank(message = "поле пусте")
    @Email
    private String email;
    @NotBlank(message = "поле пусте")
    //@Size(min = 4, max = 22, message = "Пароль має бути від {4} до {22} символів")
    private String password;
    @Min(0)
    private int age;
    @Pattern(regexp = "\\d{12}",message = "phon number muct be 12 digsts")
    private String phoneNumber;
    //private String roles;

    public User(Long id, String name, String email, int age, String phoneNumber,String password) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.password=password;

    }

}