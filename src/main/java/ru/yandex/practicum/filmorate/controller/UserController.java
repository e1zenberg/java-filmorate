package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();     // in-memory хранилище
    private final AtomicInteger idGen = new AtomicInteger(0);     // генератор id

    @PostMapping
    public User createUser(@RequestBody User user) {
        prepareAndValidateUser(user);
        int id = idGen.incrementAndGet();
        user.setId(id);
        users.put(id, user);
        log.info("Создан пользователь {} (id={})", user.getLogin(), id);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        prepareAndValidateUser(user);
        Integer id = user.getId();
        if (id == null || !users.containsKey(id)) {
            log.error("Пользователь id={} не найден", id);
            throw new ValidationException("Пользователь с id=" + id + " не найден");
        }
        users.put(id, user);
        log.info("Обновлён пользователь {} (id={})", user.getLogin(), id);
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    // простая подготовка и проверка полей пользователя
    private void prepareAndValidateUser(User user) {
        // проверяем email
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Неправильный email");
            throw new ValidationException("Email должен содержать '@' и не быть пустым");
        }
        // проверяем login
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Неправильный login");
            throw new ValidationException("Login не должен быть пустым или содержать пробелы");
        }
        // если имя пустое, используем login
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        // проверяем, что дата рождения указана
        if (user.getBirthday() == null) {
            log.error("Дата рождения не указана");
            throw new ValidationException("Дата рождения не может быть пустой");
        }
        // проверяем, что дата рождения не в будущем
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения '{}' указана в будущем", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}