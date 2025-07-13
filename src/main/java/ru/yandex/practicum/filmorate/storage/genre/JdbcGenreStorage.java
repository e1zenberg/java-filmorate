package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

/**
 * DAO-реализация GenreStorage на базе H2 и JdbcTemplate.
 */
@Repository
public class JdbcGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "SELECT genre_id, name FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre g = new Genre();
            g.setId(rs.getInt("genre_id"));
            g.setName(rs.getString("name"));
            return g;
        });
    }

    @Override
    public Genre getGenreById(int id) {
        String sql = "SELECT genre_id, name FROM genres WHERE genre_id = ?";
        List<Genre> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre g = new Genre();
            g.setId(rs.getInt("genre_id"));
            g.setName(rs.getString("name"));
            return g;
        }, id);

        if (list.isEmpty()) {
            throw new NotFoundException("Genre with id=" + id + " not found");
        }
        return list.get(0);
    }
}
