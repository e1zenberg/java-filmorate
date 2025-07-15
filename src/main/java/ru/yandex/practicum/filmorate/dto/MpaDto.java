package ru.yandex.practicum.filmorate.dto;

import ru.yandex.practicum.filmorate.model.Mpa;

/**
 * DTO для MPA-рейтинга.
 */
public class MpaDto {
    // Идентификатор рейтинга
    private int id;
    // Кодовое название рейтинга
    private String name;

    // Пустой конструктор для JSON
    public MpaDto() {
    }

    /**
     * Полный конструктор.
     */
    public MpaDto(int id, String name) {
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
     * Преобразует DTO в доменную модель Mpa.
     */
    public Mpa toModel() {
        return new Mpa(id, name);
    }
}
