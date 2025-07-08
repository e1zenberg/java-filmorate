package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();      // in-memory хранилище
    private final AtomicInteger idGen = new AtomicInteger(0);     // генератор id
    private static final LocalDate FIRST_CINEMA_DATE =
            LocalDate.of(1895, Month.DECEMBER, 28);                  // первая дата показа

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validateFilm(film);
        int id = idGen.incrementAndGet();
        film.setId(id);
        films.put(id, film);
        log.info("Добавлен фильм {} (id={})", film.getName(), id);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validateFilm(film);
        Integer id = film.getId();
        if (id == null || !films.containsKey(id)) {
            log.error("Фильм id={} не найден", id);
            throw new ValidationException("Фильм с id=" + id + " не найден");
        }
        films.put(id, film);
        log.info("Обновлён фильм {} (id={})", film.getName(), id);
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    // простая валидация полей фильма
    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Пустое название фильма");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Слишком длинное описание");
            throw new ValidationException("Описание должно быть ≤200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(FIRST_CINEMA_DATE)) {
            log.error("Дата релиза слишком ранняя");
            throw new ValidationException("Релиз не ранее 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.error("Неположительная длительность");
            throw new ValidationException("Длительность должна быть >0");
        }
    }
}