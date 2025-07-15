package ru.yandex.practicum.filmorate.dto;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO для передачи данных о фильме через API.
 */
public class FilmDto {
    // Идентификатор фильма
    private long id;
    // Название фильма
    private String name;
    // Описание фильма
    private String description;
    // Дата выхода
    private LocalDate releaseDate;
    // Длительность в минутах
    private int duration;
    // MPA-рейтинг
    private MpaDto mpa;
    // Список жанров
    private List<GenreDto> genres;

    // Пустой конструктор для JSON-сериализации
    public FilmDto() {
    }

    /**
     * Полный конструктор.
     */
    public FilmDto(long id,
                   String name,
                   String description,
                   LocalDate releaseDate,
                   int duration,
                   MpaDto mpa,
                   List<GenreDto> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

    // Геттеры и сеттеры для всех полей

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public MpaDto getMpa() {
        return mpa;
    }

    public void setMpa(MpaDto mpa) {
        this.mpa = mpa;
    }

    public List<GenreDto> getGenres() {
        return genres;
    }

    public void setGenres(List<GenreDto> genres) {
        this.genres = genres;
    }

    /**
     * Преобразует DTO в доменную модель Film.
     */
    public Film toModel() {
        Film film = new Film();
        film.setId(id);                              // устанавливаем id
        film.setName(name);                          // название
        film.setDescription(description);            // описание
        film.setReleaseDate(releaseDate);            // дата выхода
        film.setDuration(duration);                  // длительность
        film.setMpa(mpa.toModel());                  // MPA через вложенный DTO

        // Преобразуем список GenreDto в Set<Genre>
        Set<Genre> genreSet = genres == null
                ? Set.of()
                : genres.stream()
                .map(GenreDto::toModel)        // каждый DTO в модель
                .collect(Collectors.toSet()); // собираем в Set
        film.setGenres(genreSet);                    // устанавливаем жанры

        return film;
    }
}
