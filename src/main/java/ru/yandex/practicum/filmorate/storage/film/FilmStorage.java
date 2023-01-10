package ru.yandex.practicum.filmorate.storage.film;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;

public interface FilmStorage {
    HashMap<Integer, Film> getFilms();
    List<Film> findAll();

    Film create (Film film);

    Film update(Film film);

    Film findFilm(int id);

    void setLikes(int id, int userId);

    void deleteLike(int id, int userId);

    List<Film> getPopularFilms(int count);

}
