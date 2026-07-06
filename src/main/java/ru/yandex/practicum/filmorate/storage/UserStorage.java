package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User add(User user);

    User update(User user);

    Collection<User> getAll();

    Optional<User> getById(int id);

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Collection<Integer> getFriends(int userId);
}
