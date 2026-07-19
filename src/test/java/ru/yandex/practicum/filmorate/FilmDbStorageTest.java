package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({
        FilmDbStorage.class,
        FilmRowMapper.class
})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM films");
    }

    private Film createFilm() {
        Film film = new Film();

        film.setName("Test film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);

        film.setMpa(mpa);

        return film;
    }

    @Test
    void shouldAddFilm() {
        Film film = createFilm();

        Film savedFilm = filmDbStorage.add(film);

        assertThat(savedFilm.getId()).isNotNull();

        assertThat(savedFilm.getName()).isEqualTo("Test film");
    }


    @Test
    void shouldFindFilmById() {
        Film film = filmDbStorage.add(createFilm());

        Film result = filmDbStorage.getById(film.getId()).orElseThrow();

        assertThat(result.getName()).isEqualTo("Test film");
    }

    @Test
    void shouldUpdateFilm() {
        Film film = filmDbStorage.add(createFilm());

        film.setName("Updated film");

        Film updated = filmDbStorage.update(film);

        assertThat(updated.getName()).isEqualTo("Updated film");
    }

    @Test
    void shouldReturnAllFilms() {
        filmDbStorage.add(createFilm());
        assertThat(filmDbStorage.getAll()).hasSize(1);
    }
}