package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Рейтинг фильма (MPA).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {
    /** Идентификатор рейтинга */
    private Integer id;
    /** Наименование рейтинга, например G, PG-13 */
    private String name;
}