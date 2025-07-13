package ru.yandex.practicum.filmorate.storage.jdbc.user;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
@Primary
public class JdbcUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public JdbcUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        int newId = keyHolder.getKey().intValue();
        user.setId(newId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        int updated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        if (updated == 0) {
            throw new NotFoundException("User with id=" + user.getId() + " not found");
        }
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT user_id, email, login, name, birthday FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, id);
        if (users.isEmpty()) {
            throw new NotFoundException("User with id=" + id + " not found");
        }
        return users.get(0);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        // Проверяем, что оба пользователя существуют
        getUserById(userId);
        getUserById(friendId);
        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'CONFIRMED')";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = """
            SELECT u.user_id, u.email, u.login, u.name, u.birthday
              FROM users u
              JOIN friends f ON u.user_id = f.friend_id
             WHERE f.user_id = ?
             ORDER BY u.user_id
            """;
        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        String sql = """
            SELECT u.user_id, u.email, u.login, u.name, u.birthday
              FROM users u
              JOIN friends f1 ON u.user_id = f1.friend_id
              JOIN friends f2 ON u.user_id = f2.friend_id
             WHERE f1.user_id = ? AND f2.user_id = ?
             ORDER BY u.user_id
            """;
        return jdbcTemplate.query(sql, this::mapRowToUser, userId, otherUserId);
    }

    /** Маппер User из ResultSet */
    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("user_id"));
        u.setEmail(rs.getString("email"));
        u.setLogin(rs.getString("login"));
        u.setName(rs.getString("name"));
        u.setBirthday(rs.getDate("birthday").toLocalDate());
        return u;
    }
}
