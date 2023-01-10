package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.ActionHasAlreadyDoneException;

import ru.yandex.practicum.filmorate.exeptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll(){
        return userStorage.findAll();
    }

    public User create(User user) throws ValidationException {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) throws ValidationException {
        return userStorage.update(user);
    }

    public User findUser(int id) { // получение пользователя по ID.
        return userStorage.findUser(id);
    }

    public void addToFriendList(int id, int friendId) { //добавление в друзья
            if(!findUser(id).getFriends().isEmpty() && findUser(id).getFriends().contains((long)friendId)) {
                throw new ActionHasAlreadyDoneException("Friend has already added");
            } else if(friendId<=0) {
                throw new ObjectNotFoundException("Wrong id.");
            } else {
                userStorage.addToFriendList(id, friendId);
            }
    }

    public void removeFromFriendList(int id, int friendId) {
        userStorage.removeFromFriendList(id, friendId);
    }

    public List<User> getFriendList(int id) {
        return userStorage.getFriendList(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

}
