package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.validation.UserValid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
public class User {
    private int id;

    @NotBlank
    @Email
    @Size(min = 7)
    private String email;

    @NotBlank
    @Size(min = 3)
    private String login;

    @Size(max = 256)
    private String name;

    @Past
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();

    public void setFriends(int id) {
        friends.add((long) id);
    }

    public void deleteFriend(int id){
        friends.remove((long) id);
    }

}
