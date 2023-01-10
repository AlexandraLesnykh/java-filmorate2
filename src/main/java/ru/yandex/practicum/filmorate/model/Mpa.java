package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class Mpa {

    @Positive
    private Integer id;

    @NotNull
    private String name;
}