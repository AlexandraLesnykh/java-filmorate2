package ru.yandex.practicum.filmorate.model;
import lombok.Data;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class Genre {

    @Positive
    private Integer id;

    @Size(min = 3)
    private String name;
}
