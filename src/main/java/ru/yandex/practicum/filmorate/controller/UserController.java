package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private int nextId = 1;

    @PostMapping
    public User create(@RequestBody User user) {
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(nextId++);
        users.put(user.getId(), user);

        log.info("Создан пользователь {}", user.getLogin());
        return user;
    }



    @PutMapping
    public User update(@RequestBody User user) {
        validateUser(user);
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.error("Пользователь {} не найден", user.getId());

            throw new ValidationException("Пользователь не найден");
        }


        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.info("Обновлён пользователь {}", user.getLogin());
        return user;
    }



    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации email");
            throw new ValidationException("Некорректный email");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Ошибка валидации login");
            throw new ValidationException("Некорректный логин");
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}