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

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Добавление фильма: {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Обновление фильма: {}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Запрос списка всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("Запрос фильма по ID={}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping(LIKE_PATH)
    public ResponseEntity<Void> addLike(
            @PathVariable("id") int filmId,
            @PathVariable int userId
    ) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);
        filmService.addLike(filmId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(LIKE_PATH)
    public ResponseEntity<Void> removeLike(
            @PathVariable("id") int filmId,
            @PathVariable int userId
    ) {
        log.info("Пользователь {} убирает лайк с фильма {}", userId, filmId);
        filmService.removeLike(filmId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    public List<Film> getPopular(
            @RequestParam(value = "count", defaultValue = "10") int count
    ) {
        log.info("Запрос {} самых популярных фильмов", count);
        return filmService.getPopular(count);
    }
}
