package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

//Сервис для работы с пользователями и функцией "друзья"

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

    // Добавляет пользователя friendId в друзья пользователя userId, создаёт взаимную связь.
    public void addFriend(int userId, int friendId) {
        log.info("Пользователь {} добавляет в друзья пользователя {}", userId, friendId);
        // Убеждаемся, что оба пользователя существуют
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        // Добавляем каждого в список друзей другого
        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    // Удаляет пользователя friendId из друзей пользователя userId, удаляет также обратную связь.
    public void removeFriend(int userId, int friendId) {
        log.info("Пользователь {} удаляет из друзей пользователя {}", userId, friendId);
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        // Убираем друг друга из списков, если они там есть
        Optional.ofNullable(friends.get(userId)).ifPresent(set -> set.remove(friendId));
        Optional.ofNullable(friends.get(friendId)).ifPresent(set -> set.remove(userId));
    }

    // Возвращает множество друзей пользователя userId.
    public Set<User> getFriends(int userId) {
        log.info("Получение списка друзей для пользователя {}", userId);
        userStorage.getUserById(userId);
        return Optional.ofNullable(friends.get(userId))
                .orElse(Collections.emptySet())
                .stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }

    //Возвращает множество общих друзей пользователей userId и otherId.
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