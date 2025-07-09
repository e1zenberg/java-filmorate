package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // Пользователь userId ставит лайк фильму filmId.
    public void addLike(int filmId, int userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);
        // Проверяем, что фильм и пользователь существуют
        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        // Добавляем пользователя в множество лайкнувших
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }


    // Пользователь userId удаляет лайк с фильма filmId.
    public void removeLike(int filmId, int userId) {
        log.info("Пользователь {} убирает лайк с фильма {}", userId, filmId);
        // Проверяем, что фильм и пользователь существуют
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        // Убираем пользователя из множества лайкнувших, если оно есть
        Optional.ofNullable(likes.get(filmId)).ifPresent(set -> set.remove(userId));
    }


    // Возвращает список первых count фильмов, отсортированных по количеству лайков (по убыванию).
    public List<Film> getPopular(int count) {
        log.info("Получение {} самых популярных фильмов", count);
        // Собираем все фильмы вместе с количеством лайков (0, если нет записей)
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) ->
                        likes.getOrDefault(f.getId(), Collections.emptySet()).size()
                ).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}