package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAll() {
        String sql = """
                SELECT *
                FROM genres
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        });
    }

    @Override
    public Optional<Genre> getById(int id) {
        String sql = """
                SELECT *
                FROM genres
                WHERE id=?
                """;

        List<Genre> genres = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getInt("id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                },
                id
        );

        if (genres.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(genres.get(0));
    }
}