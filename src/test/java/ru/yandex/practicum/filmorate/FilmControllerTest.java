package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertThrows;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

class FilmControllerTest {


    FilmStorage filmStorage = new InMemoryFilmStorage();
    FilmService filmService = new FilmService(filmStorage);
    FilmController filmController = new FilmController(filmService);
    UserStorage userStorage = new InMemoryUserStorage();
    UserService userService = new UserService(userStorage);
    UserController userController = new UserController(userService);

    @Test
    void shouldRejectEmptyFilmName() {
        Film film = new Film();
        film.setName("");
        film.setReleaseDate(LocalDate.of(2000,1,1));
        film.setDuration(100);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldRejectLongDescription() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000,1,1));
        film.setDuration(100);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldRejectBadEmail() {
        User user = new User();
        user.setEmail("wrong");
        user.setLogin("login");
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldRejectFutureBirthday() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("user");
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }
}
