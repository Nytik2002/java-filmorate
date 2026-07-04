package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;



@Service
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        validate(film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        if (film.getId() == null || filmStorage.getById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }

        validate(film);
        return filmStorage.update(film);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(int id) {
        return filmStorage.getById(id).orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public void addLike(int filmId, int userId) {
        Film film = getById(filmId);

        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        film.getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getById(filmId);

        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        film.getLikes().remove(userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getAll().stream().sorted((a, b) -> Integer.compare(b.getLikes().size(), a.getLikes()
                        .size())).limit(count).toList();
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не должно превышать 200 символов");
        }

        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Некорректная дата релиза");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }
}