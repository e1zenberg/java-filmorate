package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

// Сервис для работы с фильмами и их лайками.
@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    // Хранение лайков: ключ — ID фильма, значение — множество ID пользователей, поставивших лайк
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    /**
     * Создаёт новый фильм с валидацией.
     */
    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    /**
     * Обновляет фильм (проверяет существование и валидирует).
     */
    public Film updateFilm(Film film) {
        if (film.getId() == null || filmStorage.getFilmById(film.getId()) == null) {
            throw new NotFoundException("Film with id=" + film.getId() + " not found");
        }
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    /**
     * Валидация полей фильма.
     */
    private void validateFilm(Film f) {
        if (f.getName() == null || f.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым.");
        }
        if (f.getDescription() == null || f.getDescription().length() > 200) {
            throw new ValidationException("Описание не может быть длиннее 200 символов.");
        }
        if (f.getReleaseDate() == null || f.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895.");
        }
        if (f.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной.");
        }
    }

    /**
     * Возвращает всех фильмов.
     */
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    /**
     * Возвращает фильм по ID.
     */
    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    /**
     * Пользователь userId ставит лайк фильму filmId.
     */
    public void addLike(int filmId, int userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    /**
     * Пользователь userId удаляет лайк с фильма filmId.
     */
    public void removeLike(int filmId, int userId) {
        log.info("Пользователь {} убирает лайк с фильма {}", userId, filmId);
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        Optional.ofNullable(likes.get(filmId)).ifPresent(set -> set.remove(userId));
    }

    /**
     * Возвращает список первых count фильмов, отсортированных по количеству лайков (по убыванию).
     */
    public List<Film> getPopular(int count) {
        log.info("Получение {} самых популярных фильмов", count);
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) ->
                        likes.getOrDefault(f.getId(), Collections.emptySet()).size()
                ).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
