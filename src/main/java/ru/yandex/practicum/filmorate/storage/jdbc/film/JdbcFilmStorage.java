package ru.yandex.practicum.filmorate.storage.jdbc.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

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
@Component
public class JdbcFilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public JdbcFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Получить все фильмы.
     *
     * @return список всех фильмов
     */
    public List<Film> getAllFilms() {
        String sql = "SELECT film_id, name, description, release_date, duration, mpa_id FROM films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    /**
     * Получить фильм по идентификатору.
     *
     * @param id идентификатор фильма
     * @return найденный фильм
     * @throws NotFoundException если фильм не найден
     */
    public Film getFilmById(long id) {
        String sql = "SELECT film_id, name, description, release_date, duration, mpa_id FROM films WHERE film_id = ?";
        List<Film> list = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (list.isEmpty()) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return list.get(0);
    }

    /**
     * Добавить фильм.
     *
     * @param film объект фильма
     * @return добавленный фильм с заполненным ID
     */
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        long newId = keyHolder.getKey().longValue();
        film.setId(newId);
        updateFilmGenres(newId, film.getGenres());
        return film;
    }

    /**
     * Обновить данные фильма.
     *
     * @param film объект фильма с новыми данными
     * @return обновленный фильм
     * @throws NotFoundException если фильм не найден
     */
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (updated == 0) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        updateFilmGenres(film.getId(), film.getGenres());
        return getFilmById(film.getId());
    }

    // --- Лайки ---

    /**
     * Добавить лайк фильму от пользователя.
     *
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя
     */
    public void addLike(long filmId, long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    /**
     * Удалить лайк фильма от пользователя.
     *
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя
     */
    public void removeLike(long filmId, long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    // --- Популярные фильмы ---

    /**
     * Получить топ популярных фильмов.
     *
     * @param count количество фильмов
     * @return список популярных фильмов
     */
    public List<Film> getPopular(int count) {
        String sql =
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id " +
                        "FROM films f " +
                        "LEFT JOIN likes l ON f.film_id = l.film_id " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(l.user_id) DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private void updateFilmGenres(long filmId, Set<Genre> genres) {
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, filmId);
        if (genres != null) {
            String insertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : genres) {
                jdbcTemplate.update(insertSql, filmId, genre.getId());
            }
        }
    }

    private Set<Genre> getGenresByFilmId(long filmId) {
        String sql = "SELECT g.genre_id, g.name " +
                "FROM genres g " +
                "JOIN film_genre fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
        return new LinkedHashSet<>(list);
    }

    private Mpa getMpaById(int mpaId) {
        String sql = "SELECT mpa_id, name FROM mpa WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Mpa m = new Mpa();
            m.setId(rs.getInt("mpa_id"));
            m.setName(rs.getString("name"));
            return m;
        }, mpaId);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(getMpaById(rs.getInt("mpa_id")));
        Set<Genre> genres = getGenresByFilmId(film.getId());
        film.setGenres(genres.isEmpty() ? null : genres);
        return film;
    }
}
