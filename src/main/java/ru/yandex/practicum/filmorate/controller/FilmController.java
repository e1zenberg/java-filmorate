package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        // Создаём новый фильм через хранилище
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        // Обновляем фильм через хранилище
        return filmStorage.updateFilm(film);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        // Получаем фильм по идентификатору
        return filmStorage.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable("id") int filmId,
            @PathVariable int userId
    ) {
        // Ставим лайк фильму через сервис
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(
            @PathVariable("id") int filmId,
            @PathVariable int userId
    ) {
        // Убираем лайк через сервис
        filmService.removeLike(filmId, userId);
        // Явно возвращаем 200 OK, чтобы MockMvc не воспринимал void как 404
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    public List<Film> getPopular(
            @RequestParam(value = "count", defaultValue = "10") int count
    ) {
        // Возвращаем наиболее популярные фильмы
        return filmService.getPopular(count);
    }
}
