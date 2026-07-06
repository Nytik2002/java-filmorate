package ru.yandex.practicum.filmorate.storage;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public Film add(Film film) {
        String sql = """
                INSERT INTO films
                (
                name,
                description,
                release_date,
                duration,
                mpa_id
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()
        );

        Integer id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM films", Integer.class);
        film.setId(id);
        saveGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = """
                UPDATE films
                SET name=?,
                    description=?,
                    release_date=?,
                    duration=?,
                    mpa_id=?
                WHERE id=?
                """;

        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        jdbcTemplate.update(
                """
                DELETE FROM film_genres
                WHERE film_id=?
                """,
                film.getId()
        );

        saveGenres(film);
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        List<Film> films = jdbcTemplate.query(
                """
                SELECT *
                FROM films
                """,
                filmRowMapper
        );

        for (Film film : films) {
            film.setGenres(getGenres(film.getId()));
            film.setLikes(getLikes(film.getId()));
        }
        return films;
    }

    @Override
    public Optional<Film> getById(int id) {
        List<Film> films = jdbcTemplate.query(
                """
                SELECT *
                FROM films
                WHERE id=?
                """,
                filmRowMapper,
                id
        );

        if (films.isEmpty()) {
            return Optional.empty();
        }

        Film film = films.get(0);
        film.setGenres(getGenres(id));
        film.setLikes(getLikes(id));
        return Optional.of(film);
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null) {
            return;
        }
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(
                    """
                    INSERT INTO film_genres
                    (
                    film_id,
                    genre_id
                    )
                    VALUES (?, ?)
                    """,
                    film.getId(),
                    genre.getId()
            );
        }
    }

    private Set<Genre> getGenres(int filmId) {
        String sql = """
                SELECT g.id, g.name
                FROM genres g
                JOIN film_genres fg
                ON g.id = fg.genre_id
                WHERE fg.film_id=?
                ORDER BY g.id
                """;

        return new LinkedHashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId));
    }

    private Set<Integer> getLikes(int filmId) {
        return new LinkedHashSet<>(
                jdbcTemplate.query(
                        """
                        SELECT user_id
                        FROM likes
                        WHERE film_id=?
                        """,
                        (rs, rowNum) -> rs.getInt("user_id"),
                        filmId
                )
        );
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update(
                """
                MERGE INTO likes(film_id,user_id)
                KEY(film_id,user_id)
                VALUES (?,?)
                """,
                filmId,
                userId
        );
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update(
                """
                DELETE FROM likes
                WHERE film_id=?
                AND user_id=?
                """,
                filmId,
                userId
        );
    }
}