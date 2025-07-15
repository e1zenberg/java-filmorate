package ru.yandex.practicum.filmorate.storage.jdbc.film;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Хранилище фильмов на базе JDBC.
 */
@Primary
@Repository
public class JdbcFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public JdbcFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Вернуть все фильмы */
    @Override
    public List<Film> getAllFilms() {
        // Теперь подтягиваем m.name как mpa_name
        String sql = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                   f.mpa_id, m.name AS mpa_name
              FROM films f
              JOIN mpa m ON f.mpa_id = m.mpa_id
        """;
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    /** Получить фильм по идентификатору. */
    @Override
    public Film getFilmById(int id) {
        String sql = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                   f.mpa_id, m.name AS mpa_name
              FROM films f
              JOIN mpa m ON f.mpa_id = m.mpa_id
             WHERE f.film_id = ?
        """;
        List<Film> list = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (list.isEmpty()) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return list.get(0);
    }

    /** Добавить фильм. */
    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        updateFilmGenres(film.getId(), film.getGenres());
        return film;
    }

    /** Обновить фильм. */
    @Override
    public Film updateFilm(Film film) {
        String sql = """
            UPDATE films
               SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
             WHERE film_id = ?
        """;
        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (updated == 0) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        updateFilmGenres(film.getId(), film.getGenres());
        return film;
    }

    /** Добавить лайк от пользователя к фильму */
    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    /** Убрать лайк от пользователя */
    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    /** Вернуть топ-N популярных фильмов */
    @Override
    public List<Film> getPopular(int count) {
        String sql = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                   f.mpa_id, m.name AS mpa_name
              FROM films f
              JOIN mpa m ON f.mpa_id = m.mpa_id
              LEFT JOIN likes l ON f.film_id = l.film_id
          GROUP BY f.film_id
          ORDER BY COUNT(l.user_id) DESC
             LIMIT ?
        """;
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    // — внутренние методы для работы с жанрами и маппингом

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        // Теперь заполняем и имя рейтинга
        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"))); // добавлено получение mpa_name

        // Получаем все жанры
        Set<Genre> genres = getGenresByFilmId(film.getId());
        // Если жанров нет — оставляем null, как ожидают тесты
        film.setGenres(genres.isEmpty() ? null : genres);                   // изменено: empty -> null

        return film;
    }

    private void updateFilmGenres(long filmId, Set<Genre> genres) {
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, filmId);
        if (genres != null) {
            for (Genre genre : genres) {
                String insertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
                jdbcTemplate.update(insertSql, filmId, genre.getId());
            }
        }
    }

    private Set<Genre> getGenresByFilmId(long filmId) {
        String sql = """
            SELECT g.genre_id, g.name
              FROM genres g
              JOIN film_genre fg ON g.genre_id = fg.genre_id
             WHERE fg.film_id = ?
        """;
        return new LinkedHashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre g = new Genre();
            g.setId(rs.getInt("genre_id"));
            g.setName(rs.getString("name"));
            return g;
        }, filmId));
    }
}
