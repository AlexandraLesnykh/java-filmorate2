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

}
