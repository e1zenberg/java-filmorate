package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Реализация UserStorage на основе in-memory HashMap.
 */
@Component  // Позволяет Spring найти и внедрить это хранилище
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger idGen = new AtomicInteger(0);  // Генератор уникальных ID

    @Override
    public User addUser(User user) {
        validate(user);  // Проверяем корректность полей пользователей перед сохранением
        int id = idGen.incrementAndGet();  // Получаем следующий уникальный id
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validate(user);  // Проверяем корректность полей перед обновлением
        int id = user.getId();
        if (!users.containsKey(id)) {
            // Если пользователя с таким ID нет в хранилище, выбрасываем NotFoundException
            throw new NotFoundException("User with id=" + id + " not found");
        }
        users.put(id, user);  // Обновляем данные существующего пользователя
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
            // Если пользователя с указанным ID нет, выбрасываем NotFoundException
            throw new NotFoundException("User with id=" + id + " not found");
        }
        return user;
    }

    // Вспомогательный метод для валидации полей User перед добавлением/обновлением
    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email must contain '@' and not be empty");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Login must not be empty or contain spaces");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());  // Если имя не задано, используем логин
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday must be in the past");
        }
    }
}
