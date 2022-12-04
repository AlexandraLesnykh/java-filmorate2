package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.validation.FilmValid;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private int id;
    @Size(max = 200)
    private String description;
    @FilmValid
    private LocalDate releaseDate;
    @Min(1)
    private long duration; //минуты.
    @NonNull
    private String name;

    public Film(int id, String description,LocalDate releaseDate, long duration, @NonNull String name) {
        this.id = id;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.name = name;
    }

    private Set<Long> likes = new HashSet<>();
    public void setLikes(int id) {
        likes.add((long) id);
    }

    public void deleteLike(int id){
        likes.remove((long) id);
    }
}
