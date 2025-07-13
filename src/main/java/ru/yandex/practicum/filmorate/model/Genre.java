package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Жанр фильма.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    /** Идентификатор жанра */
    private Integer id;
    /** Название жанра, например “Комедия” */
    private String name;
}
