package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

// Сервис для работы с пользователями и функцией "друзья"
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    // Хранение связей «пользователь - его друзья»
    // Ключ — ID пользователя, значение — множество ID его друзей
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Создание нового пользователя с валидацией.
     */
    public User createUser(User user) {
        validateUser(user);
        // По ТЗ: если имя пустое, использовать login
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    /**
     * Обновление существующего пользователя с валидацией.
     */
    public User updateUser(User user) {
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    /**
     * Валидация полей пользователя.
     * Бросает ValidationException при нарушении бизнес-правил.
     */
    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Email должен содержать символ '@'.");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

    /**
     * Добавляет пользователя friendId в друзья пользователя userId, создаёт взаимную связь.
     */
    public void addFriend(int userId, int friendId) {
        log.info("Пользователь {} добавляет в друзья пользователя {}", userId, friendId);
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    /**
     * Удаляет пользователя friendId из друзей пользователя userId, удаляет также обратную связь.
     */
    public void removeFriend(int userId, int friendId) {
        log.info("Пользователь {} удаляет из друзей пользователя {}", userId, friendId);
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        Optional.ofNullable(friends.get(userId)).ifPresent(set -> set.remove(friendId));
        Optional.ofNullable(friends.get(friendId)).ifPresent(set -> set.remove(userId));
    }

    /**
     * Возвращает множество друзей пользователя userId.
     */
    public Set<User> getFriends(int userId) {
        log.info("Получение списка друзей для пользователя {}", userId);
        userStorage.getUserById(userId);
        return Optional.ofNullable(friends.get(userId))
                .orElse(Collections.emptySet())
                .stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }

    /**
     * Возвращает множество общих друзей пользователей userId и otherId.
     */
    public Set<User> getCommonFriends(int userId, int otherId) {
        log.info("Получение общих друзей для пользователей {} и {}", userId, otherId);
        userStorage.getUserById(userId);
        userStorage.getUserById(otherId);
        Set<Integer> set1 = Optional.ofNullable(friends.get(userId)).orElse(Collections.emptySet());
        Set<Integer> set2 = Optional.ofNullable(friends.get(otherId)).orElse(Collections.emptySet());
        return set1.stream()
                .filter(set2::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }
}