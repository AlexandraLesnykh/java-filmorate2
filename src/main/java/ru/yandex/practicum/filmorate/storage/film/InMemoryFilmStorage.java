package ru.yandex.practicum.filmorate.storage.film;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.ActionHasAlreadyDoneException;
import ru.yandex.practicum.filmorate.exeptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    private final HashMap<Integer, Film> films = new HashMap<>();

    public HashMap<Integer, Film> getFilms() {
        return films;
    }
    private List<Film> filmsArrayList = new ArrayList<>();
    private int idCount = 1;
    private static Validator validator;
    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    public List<Film> findAll() {
        filmsArrayList.addAll(films.values());
        return filmsArrayList;
    }

    public Film create (@Valid Film film) throws ValidationException {
            Set<ConstraintViolation<Film>> validate = validator.validate(film);
            if (validate.size() > 0 || film.getName()=="") {
                throw new  ValidationException("Error while saving");
            } else {
                film.setId(idCount++);
                films.put(film.getId(), film);
            }
        return film;
    }

    public Film update(@Valid Film film) throws ValidationException {
            Set<ConstraintViolation<Film>> validate = validator.validate(film);
            if (validate.size() > 0 || film.getName() == "") {
                throw new ValidationException("Error while updating");
            } else {
                if (films.containsKey(film.getId())) {
                    films.remove(film.getId());
                    films.put(film.getId(), film);
                } else {
                    throw new ValidationException("Error while updating");
                }
            }
        return film;
    }

    Map<Integer, Integer> likes = new HashMap<>(); //ключ - ID фильма,
    // значения - ID пользователей, кто лайкнул
    List<Film> popularFilms = new ArrayList<>();

    @Override
    public Film findFilm(int id) { // получение пользователя по ID
        return getFilms().get(id);
    }

    @Override
    public void setLikes(int id, int userId) { //добавление лайка
        findFilm(id).setLikes(userId);
    }

    @Override
    public void deleteLike(int id, int userId) { //удаление лайка.
        findFilm(id).deleteLike(userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        popularFilms.clear();
        for (Film film : findAll()) {
            likes.put(film.getId(), film.getLikes().size());
        }

        likes = likes.entrySet()
                .stream().sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(count)
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        for (Integer id : likes.keySet()) {
            if (popularFilms.isEmpty() || !popularFilms.contains(findFilm(id))) {
                popularFilms.add(findFilm(id));
            }
        }
        return popularFilms;
    }
}
