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
import java.util.Collection;

/**
 * JDBC-реализация хранилища пользователей с использованием JdbcTemplate.
 */
@Repository
@Primary  // Делает этот бин предпочтительным при автосвязывании UserStorage
public class JdbcUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public JdbcUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        // Вставляем нового пользователя и получаем сгенерированный ключ (ID)
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
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
        // Обновляем существующего пользователя по ID
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
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
        // Получаем всех пользователей
        String sql = "SELECT id, email, login, name, birthday FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setEmail(rs.getString("email"));
            u.setLogin(rs.getString("login"));
            u.setName(rs.getString("name"));
            u.setBirthday(rs.getDate("birthday").toLocalDate());
            return u;
        });
    }

    @Override
    public User getUserById(int id) {
        // Получаем одного пользователя по ID
        String sql = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setLogin(rs.getString("login"));
                u.setName(rs.getString("name"));
                u.setBirthday(rs.getDate("birthday").toLocalDate());
                return u;
            } else {
                throw new NotFoundException("User with id=" + id + " not found");
            }
        }, id);
    }
}
