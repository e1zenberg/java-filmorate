package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import java.time.LocalDate;

@Data
// модель фильма
public class Film {
    private Integer id;               // уникальный id
    private String name;              // название, не пустое
    private String description;       // описание ≤200 символов
    private LocalDate releaseDate;    // дата релиза ≥28.12.1895
    private long duration;            // продолжительность >0
}