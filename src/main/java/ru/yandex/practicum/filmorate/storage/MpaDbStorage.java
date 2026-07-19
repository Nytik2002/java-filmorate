package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> getAll() {
        String sql = """
                SELECT *
                FROM mpa
                ORDER BY id
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Mpa mpa = new Mpa();
                    mpa.setId(rs.getInt("id"));
                    mpa.setName(rs.getString("name"));
                    return mpa;
                }
        );
    }

    @Override
    public Optional<Mpa> getById(int id) {
        String sql = """
                SELECT *
                FROM mpa
                WHERE id=?
                """;

        List<Mpa> mpaList = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Mpa mpa = new Mpa();
                    mpa.setId(rs.getInt("id"));
                    mpa.setName(rs.getString("name"));
                    return mpa;
                },
                id
        );

        if (mpaList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mpaList.get(0));
    }
}