package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exeptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.rowMapper.RowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@Qualifier
public class UserDbStorage implements UserStorage{

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public HashMap<Integer, User> getUsers() {
        return null;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery =
                "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
                        "FROM users AS u";
        return jdbcTemplate.query(sqlQuery, RowMapper::rowMapToUser);
    }

    @Override
    public User findUser(int id){
        String sqlQuery = "SELECT u.user_id, u.email, u.login, u.name, u.birthday FROM users AS u WHERE u.user_id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, RowMapper::rowMapToUser, id);
        if (users.size() != 1) {
            throw new ObjectNotFoundException("Wrong id");
        }
        return users.get(0);
    }

    @Override
    public User create(User user) throws ValidationException {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((connection) -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("User " + user.getName() + "has been added to the database. " );
        return user;
    }

    @Override
    public User update(User user) throws ValidationException {
        if (findUser(user.getId()).getId() == user.getId()) {
            String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    Date.valueOf(user.getBirthday()),
                    user.getId());
        } else {
            throw new ObjectNotFoundException("Wrong id");
        }
        log.info("User " + user.getName() + " has been updated to the database." );
        return user;
    }

    public void addToFriendList(int id, int friendId) {
        if (findUser(id) != null && findUser(friendId) != null) {
            String sqlQuery = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 1)";
            jdbcTemplate.update(sqlQuery, id, friendId);
        } else {
            throw new ObjectNotFoundException("Wrong id");
        }
        log.info("User " + findUser(id).getName() + " became friend to " + findUser(friendId).getName());
    }

    public void removeFromFriendList(int id, int friendId) {
        if (findUser(id) != null && findUser(friendId) != null) {
            String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlQuery, id, friendId);
        } else {
            throw new ObjectNotFoundException("Wrong id");
        }
        log.info("User " + findUser(id).getName() + " deleted  " + findUser(friendId).getName() + " from friendliest");
    }

    public List<User> getFriendList(int id) {
        if (findUser(id) != null) {
            String sqlQuery = "SELECT u.user_id, f.friend_id, u.email, u.login, u.name, u.birthday " +
                    "FROM friends AS f " +
                    "LEFT JOIN users AS u ON u.user_id = f.friend_id " +
                    "WHERE f.user_id = ?";
            return jdbcTemplate.query(sqlQuery, RowMapper::rowMapToUser, id);
        } else {
            throw new ObjectNotFoundException("Wrong id");
        }
    }

    public List<User> getCommonFriends(int id, int friendId) {
        if (findUser(id) != null && findUser(friendId) != null) {
            String sqlQuery = "SELECT  u.user_id, u.email, u.login, u.name, u.birthday " +
                    "FROM users AS u " +
                    "JOIN friends AS f1 ON u.USER_ID = f1.FRIEND_ID " +
                    "JOIN friends AS f2 ON f1.FRIEND_ID = f2.FRIEND_ID " +
                    "WHERE f1.USER_ID = ? AND f2.USER_ID = ?";
            return jdbcTemplate.query(sqlQuery, RowMapper::rowMapToUser, id, friendId);
        } else {
            throw new ObjectNotFoundException("Wrong id");
        }
    }
}
