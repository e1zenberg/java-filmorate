package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создаёт нового пользователя с валидацией.
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * Обновляет существующего пользователя с валидацией.
     */
    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * Возвращает всех пользователей.
     */
    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Возвращает пользователя по ID.
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    /**
     * Добавляет в друзья.
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable("id") int userId,
            @PathVariable int friendId
    ) {
        userService.addFriend(userId, friendId);
    }

    /**
     * Удаляет из друзей.
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable("id") int userId,
            @PathVariable int friendId
    ) {
        userService.removeFriend(userId, friendId);
    }

    /**
     * Список друзей пользователя.
     */
    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable("id") int userId) {
        return userService.getFriends(userId);
    }

    /**
     * Список общих друзей двух пользователей.
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(
            @PathVariable("id") int userId,
            @PathVariable int otherId
    ) {
        return userService.getCommonFriends(userId, otherId);
    }
}
