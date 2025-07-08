package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE) // все поля по умолчанию private
// модель пользователя
public class User {
    Integer id;            // уникальный id
    String email;          // email с '@'
    String login;          // логин без пробелов
    String name;           // имя (если пусто, будет login)
    LocalDate birthday;    // дата рождения ≤сегодня
}