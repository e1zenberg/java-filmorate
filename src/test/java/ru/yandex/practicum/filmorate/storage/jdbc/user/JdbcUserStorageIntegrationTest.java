package ru.yandex.practicum.filmorate.storage.jdbc.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * DAO-интеграционные тесты для JdbcUserStorage.
 */
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JdbcUserStorage.class)
@Sql({"/schema.sql", "/data.sql"})
@DisplayName("Интеграционные тесты JdbcUserStorage")
class JdbcUserStorageIntegrationTest {

    @Autowired
    private JdbcUserStorage userStorage;

    @Test
    @DisplayName("CRUD пользователей + NotFound")
    void basicCrudAndNotFound() {
        User alice = new User();
        alice.setEmail("a@a.com");
        alice.setLogin("alice");
        alice.setName("Alice");
        alice.setBirthday(LocalDate.of(1990, 1, 1));

        User saved = userStorage.addUser(alice);
        assertThat(saved.getId()).isPositive();

        List<User> all = List.copyOf(userStorage.getAllUsers());
        assertThat(all).extracting("id").contains(saved.getId());

        User fetched = userStorage.getUserById(saved.getId());
        assertThat(fetched).usingRecursiveComparison().isEqualTo(saved);

        fetched.setName("Alice Updated");
        userStorage.updateUser(fetched);
        assertThat(userStorage.getUserById(saved.getId()).getName())
                .isEqualTo("Alice Updated");

        assertThatThrownBy(() -> userStorage.getUserById(999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Friendship: add/remove/get/common")
    void friendshipOperations() {
        User u1 = userStorage.addUser(newUser("u1", "u1@x.com"));
        User u2 = userStorage.addUser(newUser("u2", "u2@x.com"));
        User u3 = userStorage.addUser(newUser("u3", "u3@x.com"));

        userStorage.addFriend(u1.getId(), u2.getId());
        userStorage.addFriend(u1.getId(), u3.getId());

        assertThat(userStorage.getFriends(u1.getId()))
                .extracting("id")
                .containsExactlyInAnyOrder(u2.getId(), u3.getId());

        assertThat(userStorage.getFriends(u2.getId())).isEmpty();

        userStorage.addFriend(u2.getId(), u3.getId());
        assertThat(userStorage.getCommonFriends(u1.getId(), u2.getId()))
                .extracting("id")
                .containsExactly(u3.getId());

        userStorage.removeFriend(u1.getId(), u2.getId());
        assertThat(userStorage.getFriends(u1.getId()))
                .doesNotContain(u2);
    }

    private static User newUser(String login, String email) {
        User u = new User();
        u.setLogin(login);
        u.setEmail(email);
        u.setName(login);
        u.setBirthday(LocalDate.of(1995, 5, 5));
        return u;
    }
}
