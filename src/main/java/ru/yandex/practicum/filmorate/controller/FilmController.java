package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new LinkedHashMap<>();

    private int nextId = 1;


    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film);
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film.getName());
        return film;
    }


    @PutMapping
    public Film update(@RequestBody Film film) {
        validateFilm(film);

        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.error("Фильм {} не найден", film.getId());
            throw new ValidationException("Фильм не найден");
        }

        films.put(film.getId(), film);
        log.info("Обновлён фильм {}", film.getName());
        return film;
    }


    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }


    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка валидации: пустое название фильма");
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Ошибка валидации: описание больше 200 символов");
            throw new ValidationException("Описание не должно превышать 200 символов");
        }


        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.error("Ошибка валидации: неправильная дата");
            throw new ValidationException("Некорректная дата релиза");
        }


        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.error("Ошибка валидации: duration <= 0");
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }
}