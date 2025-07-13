package ru.yandex.practicum.filmorate.service.user;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (user.getId() <= 0) {
            throw new ValidationException("ID пользователя должно быть положительным");
        }
        userStorage.getUserById(user.getId()); // проверка существования
        validateUser(user);
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    /** Добавить в друзья (односторонне) */
    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    /** Убрать из друзей */
    public void removeFriend(int userId, int friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    /** Список друзей */
    public List<User> getFriends(int userId) {
        return userStorage.getFriends(userId);
    }

    /** Список общих друзей */
    public List<User> getCommonFriends(int userId, int otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    /** Базовая валидация полей User */
    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Email должен содержать символ '@'");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен быть пустым или содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
