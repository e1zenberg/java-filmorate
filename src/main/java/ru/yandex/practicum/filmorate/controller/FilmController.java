package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST-контроллер для работы с фильмами.
 */
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // Создать фильм
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public FilmDto create(@RequestBody FilmDto dto) {
        Film film = dto.toModel();
        Film created = filmService.addFilm(film);
        return toDto(created);
    }

    // Обновить фильм
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public FilmDto update(@RequestBody FilmDto dto) {
        Film film = dto.toModel();
        Film updated = filmService.updateFilm(film);
        return toDto(updated);
    }

    // Получить все фильмы
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FilmDto> getAll() {
        return filmService.getAllFilms().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Получить фильм по id
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public FilmDto getById(@PathVariable int id) {
        return toDto(filmService.getFilmById(id));
    }

    // Получить популярные фильмы
    @GetMapping(value = "/popular", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FilmDto> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Добавить лайк: PUT /films/{id}/like/{userId}
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id,
                        @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    // Убрать лайк: DELETE /films/{id}/like/{userId}
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id,
                           @PathVariable int userId) {
        filmService.removeLike(id, userId);
    }

    // Вспомогательный маппер Film → FilmDto
    private FilmDto toDto(Film film) {
        List<GenreDto> genres = film.getGenres().stream()
                .distinct()
                .sorted(Comparator.comparing(Genre::getId))
                .map(g -> new GenreDto(g.getId(), g.getName()))
                .collect(Collectors.toList());
        MpaDto mpa = new MpaDto(film.getMpa().getId(), film.getMpa().getName());
        return new FilmDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpa,
                genres
        );
    }
}
