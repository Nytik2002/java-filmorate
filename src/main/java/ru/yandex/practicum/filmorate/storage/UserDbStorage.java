package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.NotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public User add(User user) {
        String sql = """
                INSERT INTO users(email, login, name, birthday)
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()));
        Integer id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM users", Integer.class);
        user.setId(id);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> getById(int id) {
        String sql = """
                SELECT *
                FROM users
                WHERE id=?
                """;

        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);

        if (users.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(users.get(0));
    }

    @Override
    public User update(User user) {
        String sql = """
                UPDATE users
                SET email=?,
                    login=?,
                    name=?,
                    birthday=?
                WHERE id=?
                """;

        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), user.getId());
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sql = """
            INSERT INTO friendship(user_id, friend_id)
            VALUES (?, ?)
            """;

        jdbcTemplate.update(
                sql,
                userId,
                friendId
        );
    }


    @Override
    public void removeFriend(int userId, int friendId) {
        String sql = """
            DELETE FROM friendship
            WHERE user_id=?
            AND friend_id=?
            """;

        jdbcTemplate.update(
                sql,
                userId,
                friendId
        );
    }

    @Override
    public Collection<Integer> getFriends(int userId) {

        if (!existsUser(userId)) {
            throw new NotFoundException("User not found");
        }

        String sql = """
        SELECT friend_id
        FROM friendship
        WHERE user_id=?
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getInt("friend_id"),
                userId
        );
    }

    private boolean existsUser(int userId) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM users
                WHERE id=?
                """,
                Integer.class,
                userId
        );

        return count != null && count > 0;
    }
}