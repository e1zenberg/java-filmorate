package ru.yandex.practicum.filmorate.dto;

public class GenreDto {
    private final int id;
    private final String name;

    public GenreDto(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}