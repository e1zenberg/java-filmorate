package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    /** Создать фильм */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    /** Обновить фильм */
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    /** Получить все фильмы */
    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    /** Получить фильм по ID */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") int id) {
        return filmService.getFilmById(id);
    }

    /** Добавить лайк фильму */
    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable int filmId, @PathVariable int userId) {
        filmService.addLike(filmId, userId);
    }

    /** Убрать лайк */
    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable int filmId, @PathVariable int userId) {
        filmService.removeLike(filmId, userId);
    }

    /** Топ-N популярных */
    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count);
    }
}
