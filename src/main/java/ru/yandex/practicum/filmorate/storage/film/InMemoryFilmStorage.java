package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory хранилище фильмов с поддержкой лайков.
 */
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();
    private final AtomicInteger idGen = new AtomicInteger(0);

    private static final LocalDate FIRST_CINEMA_DATE =
            LocalDate.of(1895, Month.DECEMBER, 28);

    @Override
    public Film addFilm(Film film) {
        validate(film);
        int id = idGen.incrementAndGet();
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validate(film);
        int id = (int) film.getId();
        if (!films.containsKey(id)) {
            throw new ValidationException("Film with id=" + id + " not found");
        }
        films.put(id, film);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new ValidationException("Film with id=" + id + " not found");
        }
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        // проверка, что фильм существует
        getFilmById(filmId);
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Optional.ofNullable(likes.get(filmId)).ifPresent(set -> set.remove(userId));
    }

    @Override
    public List<Film> getPopular(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> {
                    int l1 = likes.getOrDefault((int) f1.getId(), Collections.emptySet()).size();
                    int l2 = likes.getOrDefault((int) f2.getId(), Collections.emptySet()).size();
                    return Integer.compare(l2, l1);
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Name must not be empty");
        }
        if (film.getDescription() != null && film.getDescription().length() > 1000) {
            throw new ValidationException("Description must be <=1000 characters");
        }
        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(FIRST_CINEMA_DATE)) {
            throw new ValidationException("Release date must be after 1895-12-28");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Duration must be positive");
        }
    }
}
