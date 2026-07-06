package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.ValidationException;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(
            @Qualifier("userDbStorage") UserStorage userStorage
    ) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.add(user);
    }

    public User update(User user) {
        if (user.getId() == null || userStorage.getById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.update(user);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(int id) {
        return userStorage.getById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public void addFriend(int userId, int friendId) {
        getById(userId);
        getById(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        getById(userId);
        getById(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getFriends(int id) {
        return userStorage.getFriends(id).stream().map(this::getById).toList();
    }

    public Collection<User> getCommonFriends(int id, int otherId) {
        Collection<Integer> friends1 = userStorage.getFriends(id);
        Collection<Integer> friends2 = userStorage.getFriends(otherId);

        return friends1.stream().filter(friends2::contains).map(this::getById).toList();
    }

    private void validate(User user) {

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")
                || user.getEmail().startsWith("@") || user.getEmail().endsWith("@")) {
            throw new ValidationException("Некорректный email");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Некорректный логин");
        }

        if (user.getBirthday() == null) {
            throw new ValidationException("Дата рождения обязательна");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}