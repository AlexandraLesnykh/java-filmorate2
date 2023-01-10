package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.validation.FilmValid;

import javax.validation.constraints.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Data
public class Film {

    private int id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Positive
    private Integer duration;

    private Integer rate = 0;

    @NotNull
    private Mpa mpa = new Mpa();

    private List<Genre> genres = new ArrayList<>();

    private Set<Long> likes = new HashSet<>();

    public void setLikes(int id) {
        likes.add((long) id);
    }

    public void deleteLike(int id){
        likes.remove((long) id);
    }

    public void setMpaId(Integer id) {
        mpa.setId(id);
    }

    public void setMpaName(String name) {
        mpa.setName(name);
    }

}
