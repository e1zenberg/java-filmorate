package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * Модель фильма с рейтингом MPA и списком жанров.
 */
@Data
@Builder
@NoArgsConstructor      // <- добавлен конструктор без аргументов
@AllArgsConstructor     // <- для @Builder нужен конструктор со всеми аргументами
public class Film {
    /** Уникальный идентификатор фильма */
    private long id;

    /** Название фильма */
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    /** Описание фильма, не более 1000 символов */
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    /** Дата релиза */
    @NotNull(message = "Дата релиза не может быть пустой")
    private LocalDate releaseDate;

    /** Продолжительность в минутах, положительное число */
    @Positive(message = "Продолжительность должна быть положительной")
    private int duration;

    /** Рейтинг MPA */
    @NotNull(message = "Рейтинг MPA должен быть указан")
    private Mpa mpa;

    /** Набор жанров */
    private Set<Genre> genres;
}