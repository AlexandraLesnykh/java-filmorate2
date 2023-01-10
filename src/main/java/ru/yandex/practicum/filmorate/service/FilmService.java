package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exeptions.ActionHasAlreadyDoneException;
import ru.yandex.practicum.filmorate.exeptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmDbStorage filmDbStorage;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmDbStorage filmDbStorage) {
        this.filmStorage = filmStorage;
        this.filmDbStorage = filmDbStorage;
    }

    public List<Film> findAll(){
        return filmStorage.findAll();
    }

    public Film create(Film film){
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Error while saving");
        }
        return filmStorage.create(film);
    }

    public Film update(Film film){
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Error while saving");
        }
        return filmStorage.update(film);
    }

    public Film findFilm(int id) { // получение пользователя по ID
        return filmStorage.findFilm(id);
    }

    public void setLikes(int id, int userId) { //добавление лайка
            if (!findFilm(id).getLikes().isEmpty() && findFilm(id).getLikes().contains((long) userId)) {
                throw new ActionHasAlreadyDoneException("This user has already liked this film.");
            } else {
                filmStorage.setLikes(id, userId);
            }
    }

    public void deleteLike(int id, int userId) { //удаление лайка.
            if (userId < 0) {
                throw new ObjectNotFoundException("Wrong id.");
            }
            filmStorage.deleteLike(id, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Genre> getAllGenre() {
        return filmDbStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return filmDbStorage.getGenreById(id);
    }

    public List<Mpa> getAllMpa() {
        return filmDbStorage.getAllMpa();
    }

    public Mpa getMpaById(int id) {
        return filmDbStorage.getMpaById(id);
    }
}


