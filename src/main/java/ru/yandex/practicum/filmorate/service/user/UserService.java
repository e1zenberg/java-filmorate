package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    User addUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();

    User getUserById(int id);

    void addFriend(int userId, int friendId);

    Collection<User> getFriends(int userId);

    void removeFriend(int userId, int friendId);

    Collection<User> getCommonFriends(int userId, int otherUserId);
}
