package ru.yandex.practicum.filmorate.storage.user;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;

import java.util.HashMap;
import java.util.List;

public interface UserStorage {

    HashMap<Integer, User> getUsers();
    List<User> findAll();

    User create(User user) throws ValidationException;

    User update(User user) throws ValidationException;

    User findUser(int id);

    void addToFriendList(int id, int friendId);

    void removeFromFriendList(int id, int friendId);

    List<User> getFriendList(int id);

    List<User> getCommonFriends(int id, int otherId);
}
