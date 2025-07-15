package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory реализация UserStorage с поддержкой дружбы (односторонней).
 */
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger();

    @Override
    public User addUser(User user) {
        validateUser(user);
        int id = idGenerator.incrementAndGet();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validateUser(user);
        int id = user.getId();
        if (!users.containsKey(id)) {
            throw new ValidationException("User with id=" + id + " not found");
        }
        users.put(id, user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new ValidationException("User with id=" + id + " not found");
        }
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        // Проверяем существование обоих пользователей
        getUserById(userId);
        getUserById(friendId);
        friends
                .computeIfAbsent(userId, k -> new HashSet<>())
                .add(friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        Set<Integer> set = friends.get(userId);
        if (set != null) {
            set.remove(friendId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        getUserById(userId); // проверка существования
        return Optional.ofNullable(friends.get(userId))
                .orElse(Collections.emptySet())
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);
        Set<Integer> set1 = friends.getOrDefault(userId, Collections.emptySet());
        Set<Integer> set2 = friends.getOrDefault(otherUserId, Collections.emptySet());
        return set1.stream()
                .filter(set2::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Email должен содержать символ '@'");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен быть пустым или содержать пробелы");
        }
        LocalDate bd = user.getBirthday();
        if (bd != null && bd.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
