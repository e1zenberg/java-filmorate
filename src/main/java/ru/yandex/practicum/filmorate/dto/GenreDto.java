package ru.yandex.practicum.filmorate.dto;

import ru.yandex.practicum.filmorate.model.Genre;

/**
 * DTO для жанра фильма.
 */
public class GenreDto {
    // Идентификатор жанра
    private int id;
    // Название жанра
    private String name;

    // Пустой конструктор для JSON
    public GenreDto() {
    }

    /**
     * Полный конструктор.
     */
    public GenreDto(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Геттеры и сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Преобразует DTO в доменную модель Genre.
     */
    public Genre toModel() {
        return new Genre(id, name);
    }
}
