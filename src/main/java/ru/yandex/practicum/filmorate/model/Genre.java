package ru.yandex.practicum.filmorate.model;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class Genre {

    private Integer id;

    private String name;
}
