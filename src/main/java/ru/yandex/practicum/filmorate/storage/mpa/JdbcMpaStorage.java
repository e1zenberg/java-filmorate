package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

/**
 * DAO-реализация MpaStorage на базе H2 и JdbcTemplate.
 */
@Repository
public class JdbcMpaStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public JdbcMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT mpa_id, name FROM mpa ORDER BY mpa_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mpa m = new Mpa();
            m.setId(rs.getInt("mpa_id"));
            m.setName(rs.getString("name"));
            return m;
        });
    }

    @Override
    public Mpa getMpaById(int id) {
        String sql = "SELECT mpa_id, name FROM mpa WHERE mpa_id = ?";
        List<Mpa> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mpa m = new Mpa();
            m.setId(rs.getInt("mpa_id"));
            m.setName(rs.getString("name"));
            return m;
        }, id);

        if (list.isEmpty()) {
            throw new NotFoundException("MPA with id=" + id + " not found");
        }
        return list.get(0);
    }
}
