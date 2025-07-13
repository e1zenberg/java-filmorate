package ru.yandex.practicum.filmorate.dto;

public class MpaDto {
    private final int id;
    private final String name;

    public MpaDto(int id, String name) {
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
