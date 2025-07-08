package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
// модель пользователя
public class User {
    private Integer id;            // уникальный id
    private String email;          // email с '@'
    private String login;          // логин без пробелов
    private String name;           // имя (если пусто, будет login)
    private LocalDate birthday;    // дата рождения ≤сегодня
}