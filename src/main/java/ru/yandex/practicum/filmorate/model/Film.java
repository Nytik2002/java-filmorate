package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedHashSet;

@Data

public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    private Mpa mpa;
    private Set<Genre> genres = new LinkedHashSet<>();
    private Set<Integer> likes = new HashSet<>();
}