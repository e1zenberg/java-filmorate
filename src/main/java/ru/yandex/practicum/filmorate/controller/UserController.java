package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.List;

/**
 * REST-контроллер для работы с пользователями:
 * создание, обновление, получение и управление дружбой.
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /users — создать нового пользователя.
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Создание пользователя: {}", user);
        return userService.addUser(user);
    }

    /**
     * PUT /users — обновить информацию о пользователе.
     */
    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Обновление пользователя: {}", user);
        return userService.updateUser(user);
    }

    /**
     * GET /users — получить список всех пользователей.
     */
    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрос списка всех пользователей");
        return userService.getAllUsers();
    }

    /**
     * GET /users/{id} — получить пользователя по ID.
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("Запрос пользователя по ID={}", id);
        return userService.getUserById(id);
    }

    /**
     * PUT /users/{id}/friends/{friendId} — добавить в друзья.
     */
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(
            @PathVariable("id") int userId,
            @PathVariable int friendId
    ) {
        log.info("Пользователь {} добавляет в друзья пользователя {}", userId, friendId);
        userService.addFriend(userId, friendId);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /users/{id}/friends/{friendId} — убрать из друзей.
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(
            @PathVariable("id") int userId,
            @PathVariable int friendId
    ) {
        log.info("Пользователь {} удаляет из друзей пользователя {}", userId, friendId);
        userService.removeFriend(userId, friendId);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /users/{id}/friends — список всех друзей пользователя.
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") int userId) {
        log.info("Запрос списка друзей пользователя {}", userId);
        return userService.getFriends(userId);
    }

    /**
     * GET /users/{id}/friends/common/{otherId} — общие друзья двух пользователей.
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable("id") int userId,
            @PathVariable("otherId") int otherUserId
    ) {
        log.info("Запрос общих друзей пользователей {} и {}", userId, otherUserId);
        return userService.getCommonFriends(userId, otherUserId);
    }
}
