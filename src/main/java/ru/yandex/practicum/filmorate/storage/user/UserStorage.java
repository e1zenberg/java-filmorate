package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.List;

// Интерфейс хранилища пользователей: CRUD + дружба (односторонняя).
public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();

    User getUserById(int id);

    // Добавить в друзья (односторонне)
    void addFriend(int userId, int friendId);

    // Убрать из друзей
    void removeFriend(int userId, int friendId);

    // Список всех друзей пользователя
    List<User> getFriends(int userId);

    // Список общих друзей двух пользователей
    List<User> getCommonFriends(int userId, int otherUserId);
}