package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User addUser(User user) {
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() <= 0) {
            throw new ValidationException("User id must be positive");
        }
        User existing = userStorage.getUserById(user.getId());
        if (existing == null) {
            throw new NotFoundException("User id=" + user.getId() + " not found");
        }
        validateUser(user);
        return userStorage.updateUser(user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(int id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("User id=" + id + " not found");
        }
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User user   = getUserById(userId);
        User friend = getUserById(friendId);
        userStorage.addFriend(userId, friendId);
    }

    @Override
    public Collection<User> getFriends(int userId) {
        getUserById(userId);
        return userStorage.getFriends(userId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null
                || user.getEmail().isBlank()
                || !user.getEmail().contains("@")) {
            throw new ValidationException("Email must contain '@'");
        }
        if (user.getLogin() == null
                || user.getLogin().isBlank()
                || user.getLogin().contains(" ")) {
            throw new ValidationException("Login must not be blank or contain spaces");
        }
        if (user.getBirthday() == null
                || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday must not be in the future");
        }
    }
}
