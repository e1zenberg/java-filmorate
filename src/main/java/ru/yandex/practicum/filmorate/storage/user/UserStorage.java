package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

/**
 * Интерфейс для хранилища пользователей.
 * Определяет базовые CRUD-операции.
 */
public interface UserStorage {

    /**
     * Сохраняет нового пользователя в хранилище.
     * @param user объект пользователя без id
     * @return тот же объект, но с присвоенным id
     */
    User addUser(User user);

    /**
     * Обновляет данные существующего пользователя.
     * @param user объект пользователя с уже существующим id
     * @return обновлённый объект
     */
    User updateUser(User user);

    /**
     * Возвращает список всех пользователей.
     */
    Collection<User> getAllUsers();

    /**
     * Возвращает пользователя по его id.
     * @param id идентификатор пользователя
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если пользователь не найден
     */
    User getUserById(int id);
}