package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exeptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.rowMapper.RowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
@Qualifier
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public HashMap<Integer, Film> getFilms() {
        return null;
    }

    @Override
    public Film findFilm(int id){
        String sqlQuery = "select f.film_id, f.title, f.description, f.release_date, f.duration, f.MPA_ID, m.MPA_NAME\n" +
                "FROM FILMS AS f\n" +
                "         LEFT JOIN MPA as m on f.MPA_ID = m.MPA_ID\n" +
                "WHERE f.FILM_ID = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, RowMapper::rowMapToFilm, id);
        films.forEach(f -> f.setGenres(getGenres(f.getId())));
        if (films.size() != 1) {
            throw new ObjectNotFoundException("Wrong id");
        }
        return films.get(0);
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select f.film_id, f.title, f.description, f.release_date, f.duration, f.MPA_ID," +
                " m.MPA_NAME as mpa_name \n" +
                "FROM FILMS AS f\n" +
                "         LEFT JOIN MPA as m on f.MPA_ID = m.MPA_ID\n";
        List<Film> films = jdbcTemplate.query(sqlQuery, RowMapper::rowMapToFilm);
        films.forEach(f -> f.setGenres(getGenres(f.getId())));
        return films;
    }

    @Override
    @Transactional
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO films (TITLE, description, release_date, duration, RATE, MPA_ID) " +
                "values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((connection) -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        addFilmGenre(film);
        log.info("The film " + film.getName() + " has been added.");
        return film;
    }

    @Override
    public Film update(Film film) {
        if (findFilm(film.getId()) != null) {
           String sqlQuery = "UPDATE films SET " +
                    "TITLE = ?, description = ?, release_date = ?, duration = ?, RATE = ?, MPA_ID = ? " +
                    "WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getRate(),
                    film.getMpa().getId(),
                    film.getId());
                sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
            jdbcTemplate.update(sqlQuery, film.getId());
                List<Genre> genres = new ArrayList<>(film.getGenres()).stream().distinct().collect(Collectors.toList());
                film.setGenres(genres);
                        addFilmGenre(film);
        } else {
            throw new ObjectNotFoundException("Wrong id");
        }
        log.info("The film " + film.getName() + " has been updated." );
        return film;
    }

    private void addFilmGenre(Film film) {
        if (film.getGenres() != null) {
            String sqlQuery = "INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?, ?)";
            List<Genre> genres = new ArrayList<>(film.getGenres());
            jdbcTemplate.batchUpdate(sqlQuery, genres, genres.size(),
                    (PreparedStatement ps, Genre genre) -> {
                        ps.setLong(1, film.getId());
                        ps.setInt(2, genre.getId());
                    }
            );
        }
    }

    private List<Genre> getGenres(int id) {
        String sqlQuery = "SELECT fg.genre_id, g.GENRE\n" +
                "                        FROM FILM_GENRE AS fg\n" +
                "                        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id\n" +
                "                        WHERE fg.film_id = ?";
        return jdbcTemplate.query(sqlQuery, RowMapper::rowMapToGenre, id);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery =
                "SELECT f.film_id, f.TITLE, f.description, f.release_date, f.duration, f.rate, f.mpa_id, " +
                        "m.MPA_NAME AS mpa_name " +
                        "FROM films AS f " +
                        "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                        "ORDER BY f.rate DESC " +
                        "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, RowMapper::rowMapToFilm, count);
        films.forEach(f -> f.setGenres(getGenres(f.getId())));
        return films;
    }

    @Override
    public void setLikes(int id, int userId) {
        if (findFilm(id).getLikes().isEmpty() && !findFilm(id).getLikes().contains((long) userId)) {
            String sqlQuery = "INSERT INTO FILM_LIKES (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, id, userId);
            sqlQuery = "UPDATE films SET rate = rate + 1 WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery, id);
            log.info("User with id" + id + " liked the film " + findFilm(id).getName());
        } else {
            throw new ObjectNotFoundException("Wrong id");
        }
    }

    public void deleteLike(int id, int userId) {
        if (findFilm(id) != null) {
            String sqlQuery = "DELETE FROM FILM_LIKES WHERE film_id = ? AND user_id = ?";
            jdbcTemplate.update(sqlQuery, id, userId);
            log.info("User with id " + id + " deleted the like from the film " +
                    findFilm(id).getName());
        } else {
            throw new ObjectNotFoundException("Wrong id");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT *\n" +
                "FROM GENRES\n" +
                "ORDER BY GENRE_ID";
        return jdbcTemplate.query(sqlQuery, RowMapper::rowMapToGenre);
    }

    @Override
    public Genre getGenreById(Integer id) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, RowMapper::rowMapToGenre, id);
        if (genres.size() != 1) {
            throw new ObjectNotFoundException("Wrong id");
        }
        return genres.get(0);
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(sqlQuery, RowMapper::rowMapToMpa);
    }

    @Override
    public Mpa getMpaById(Integer id) {
        String sqlQuery = "SELECT * FROM MPA WHERE MPA_ID = ?";
        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, RowMapper::rowMapToMpa, id);
        if (mpas.size() != 1) {
            throw new ObjectNotFoundException("Wrong id");
        }
        return mpas.get(0);
    }

}
