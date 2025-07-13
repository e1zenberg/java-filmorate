package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final String LIKE_PATH = "/{id}/like/{userId}";

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Создаёт новый фильм с валидацией.
     */
    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    /**
     * Обновляет существующий фильм с проверкой и валидацией.
     */
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    /**
     * Возвращает все фильмы.
     */
    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    /**
     * Возвращает фильм по ID.
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    /**
     * Ставит лайк фильму.
     */
    @PutMapping(LIKE_PATH)
    public void addLike(
            @PathVariable("id") int filmId,
            @PathVariable int userId
    ) {
        filmService.addLike(filmId, userId);
    }

    /**
     * Убирает лайк с фильма.
     */
    @DeleteMapping(LIKE_PATH)
    public ResponseEntity<Void> removeLike(
            @PathVariable("id") int filmId,
            @PathVariable int userId
    ) {
        filmService.removeLike(filmId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает самые популярные фильмы.
     */
    @GetMapping("/popular")
    public List<Film> getPopular(
            @RequestParam(value = "count", defaultValue = "10") int count
    ) {
        return filmService.getPopular(count);
    }
}