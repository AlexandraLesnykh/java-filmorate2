        package ru.yandex.practicum.filmorate;

        import lombok.RequiredArgsConstructor;
        import org.junit.jupiter.api.Assertions;
        import org.junit.jupiter.api.Test;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
        import org.springframework.boot.test.context.SpringBootTest;
        import ru.yandex.practicum.filmorate.model.Film;
        import ru.yandex.practicum.filmorate.model.User;
        import ru.yandex.practicum.filmorate.service.FilmService;
        import ru.yandex.practicum.filmorate.service.UserService;

        import java.time.LocalDate;
        import java.util.List;
        import java.util.Optional;

        import static org.assertj.core.api.Assertions.assertThat;
        import static org.junit.jupiter.api.Assertions.assertEquals;
        import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmService filmService;
    public final UserService userService;

    private static Film getFilm(String name, String description, LocalDate releaseDate, Integer duration,
                                Integer rate, Integer mpa_id) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setRate(rate);
        film.setMpaId(mpa_id);
        return film;
    }

    private static User getUser(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }

    Film film = getFilm("Film1", "Description", LocalDate.parse("1990-01-15"),
            90, 2, 1);
    @Test
    void createFilmTest() {

        filmService.create(film);
        Optional<Film> userOptional = Optional.ofNullable(filmService.findFilm(film.getId()));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("id", film.getId())
                                .hasFieldOrPropertyWithValue("name", "Матрица")
                                .hasFieldOrPropertyWithValue("rate", 4)
                );
    }

    @Test
    void createFilmBlankNameTest() {
        film.setName("");
        Assertions.assertTrue(dtoHasErrorMessage(filmService.createFilm(film), "name should not be blank"));
    }

    @Test
    void createFilmTooLongDescriptionTest() {
        Film film = getFilm("Лето",
                "Очень очень очень очень очень очень очень очень очень очень очень очень очень очень " +
                        "очень очень очень очень очень очень очень очень очень очень очень очень очень очень " +
                        "очень очень очень длинное описание - 217 символов", LocalDate.parse("2020-08-20"),
                90, 4, 1);
        Assertions.assertTrue(dtoHasErrorMessage(filmService.createFilm(film),
                "the description should be no more than 200 characters"));
    }

    @Test
    void createFilmBadDateTest() {
        Film film = getFilm("Матч", "Описание фильма", LocalDate.parse("1600-08-20"),
                90, 4, 1);
        ValidException ex = assertThrows(
                ValidException.class,
                () -> filmService.createFilm(film)
        );
        assertEquals("The release date can't be earlier 1895-12-28", ex.getMessage());
    }

    @Test
    void createFilmNegativeDurationTest() {
        Film film = getFilm("Берегись автомобиля", "Описание фильма",
                LocalDate.parse("1960-08-20"), -30, 4, 1);
        Assertions.assertTrue(dtoHasErrorMessage(filmService.createFilm(film),
                "the duration should be positive"));
    }

    @Test
    void addLikeTest() {
        Film film = getFilm("Возвращение короля", "Описание фильма",
                LocalDate.parse("2010-08-15"), 30, 2, 1);
        User user = getUser("mymail10@yandex.ru",
                "agk10",
                "Aleksey",
                LocalDate.parse("1989-01-01"));
        filmService.createFilm(film);
        userService.createUser(user);
        filmService.addLike(film.getId(), user.getId());

        Long id = film.getId();
        Optional<Film> filmOptional = Optional.ofNullable(filmService.getFilm(id));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("rate", 3)
                );
    }

    @Test
    void deleteLikeTest() {
        Film film = getFilm("Возвращение короля2", "Описание фильма",
                LocalDate.parse("2010-08-15"), 30, 6, 1);
        User user = getUser("mymail11@yandex.ru",
                "agk11",
                "Aleksey",
                LocalDate.parse("1989-01-01"));
        filmService.createFilm(film);
        userService.createUser(user);

        filmService.addLike(film.getId(), user.getId());
        Optional<Film> filmOptional = Optional.ofNullable(filmService.getFilm(film.getId()));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("rate", 7)
                );

        filmService.deleteLike(film.getId(), user.getId());
        Optional<Film> filmOptional2 = Optional.ofNullable(filmService.getFilm(film.getId()));
        assertThat(filmOptional2)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("rate", 6)
                );
    }

    @Test
    void getTopPopularFilmsTest() {
        Film film = getFilm("Ну, погоди", "Описание фильма",
                LocalDate.parse("2000-08-15"), 30, 1000, 1);
        Film film2 = getFilm("Елки", "Описание фильма",
                LocalDate.parse("2010-12-30"), 30, 4, 1);
        filmService.createFilm(film);
        filmService.createFilm(film2);

        List<Film> topFilms = filmService.getPopularFilms(1L);

        Assertions.assertEquals(topFilms.size(), 1, "counts don't match");
        Assertions.assertEquals(topFilms.get(0).getName(), film.getName(), "films don't match");
    }

}
