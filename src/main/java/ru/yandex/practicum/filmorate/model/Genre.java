package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Жанр фильма.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    /** Идентификатор жанра */
    private Integer id;

    /** Название жанра, например "Комедия" */
    private String name;
}
