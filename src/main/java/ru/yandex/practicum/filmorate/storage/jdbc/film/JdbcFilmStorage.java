package ru.yandex.practicum.filmorate.storage.jdbc.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;

/**
 * JDBC-реализация хранилища фильмов с использованием JdbcTemplate.
 */
@Repository
@Primary  // Этот бин будет предпочтительным при автосвязывании FilmStorage
public class JdbcFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public JdbcFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        // Вставляем новый фильм и получаем сгенерированный ключ (ID)
        String sql = "INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            return ps;
        }, keyHolder);

        int newId = keyHolder.getKey().intValue();
        film.setId(newId);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        // Обновляем существующий фильм по ID
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getId());

        if (updated == 0) {
            throw new NotFoundException("Film with id=" + film.getId() + " not found");
        }
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        // Получаем все фильмы
        String sql = "SELECT id, name, description, release_date, duration FROM films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film f = new Film();
            f.setId(rs.getInt("id"));
            f.setName(rs.getString("name"));
            f.setDescription(rs.getString("description"));
            f.setReleaseDate(rs.getDate("release_date").toLocalDate());
            f.setDuration(rs.getLong("duration"));
            return f;
        });
    }

    @Override
    public Film getFilmById(int id) {
        // Получаем один фильм по ID
        String sql = "SELECT id, name, description, release_date, duration FROM films WHERE id = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                Film f = new Film();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setDescription(rs.getString("description"));
                f.setReleaseDate(rs.getDate("release_date").toLocalDate());
                f.setDuration(rs.getLong("duration"));
                return f;
            } else {
                throw new NotFoundException("Film with id=" + id + " not found");
            }
        }, id);
    }
}