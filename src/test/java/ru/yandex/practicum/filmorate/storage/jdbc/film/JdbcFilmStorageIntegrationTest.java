package ru.yandex.practicum.filmorate.storage.jdbc.film;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * DAO-интеграционные тесты для JdbcFilmStorage.
 * Перед каждым тестом подгружаем schema.sql + data.sql.
 */
@JdbcTest
@AutoConfigureTestDatabase           // используем H2 In-Memory для тестов, а не файловую
@Import(JdbcFilmStorage.class)
@Sql({"/schema.sql", "/data.sql"})
@DisplayName("Интеграционные тесты JdbcFilmStorage")
class JdbcFilmStorageIntegrationTest {

    @Autowired
    private JdbcFilmStorage filmStorage;

    @Test
    @DisplayName("Загружается предзагруженный фильм The Matrix")
    void shouldLoadInitialFilm() {
        Film matrix = filmStorage.getFilmById(1);
        assertThat(matrix.getName()).isEqualTo("The Matrix");
        assertThat(matrix.getDuration()).isEqualTo(136);
    }

    @Test
    @DisplayName("Добавление и получение фильма")
    void addAndGetFilm() {
        Film newFilm = Film.builder()
                .name("Interstellar")
                .description("Sci-fi epic")
                .releaseDate(LocalDate.of(2014, 11, 7))
                .duration(169)
                .mpa(new Mpa(1, "G"))
                .build();

        Film saved = filmStorage.addFilm(newFilm);
        int savedId = (int) saved.getId();
        Film fetched = filmStorage.getFilmById(savedId);

        assertThat(fetched).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    @DisplayName("Обновление фильма")
    void updateFilm() {
        Film film = filmStorage.getFilmById(1);
        film.setDescription("New description");
        filmStorage.updateFilm(film);

        Film updated = filmStorage.getFilmById(1);
        assertThat(updated.getDescription()).isEqualTo("New description");
    }

    @Test
    @DisplayName("Несуществующий фильм бросает NotFoundException")
    void getNonexistentFilmThrows() {
        assertThatThrownBy(() -> filmStorage.getFilmById(999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Популярные фильмы сортируются по лайкам")
    void popularSortedByLikes() {
        filmStorage.addLike(1, 10);
        filmStorage.addLike(1, 11);

        Film dune = Film.builder()
                .name("Dune")
                .description("Sci-fi saga")
                .releaseDate(LocalDate.of(2021, 10, 22))
                .duration(155)
                .mpa(new Mpa(1, "G"))
                .build();

        Film savedDune = filmStorage.addFilm(dune);
        int duneId = (int) savedDune.getId();
        filmStorage.addLike(duneId, 12);

        List<Film> popular = filmStorage.getPopular(2);
        assertThat(popular.get(0).getId()).isEqualTo(1);
        assertThat(popular.get(1).getId()).isEqualTo((long) duneId);
    }
}
