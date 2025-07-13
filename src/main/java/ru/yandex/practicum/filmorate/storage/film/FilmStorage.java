package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс хранилища фильмов: CRUD + лайки + популярные.
 */
public interface FilmStorage {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    Collection<Film> getAllFilms();
    Film getFilmById(int id);

    /** Добавить лайк от пользователя к фильму */
    void addLike(int filmId, int userId);

    /** Убрать лайк от пользователя */
    void removeLike(int filmId, int userId);

    /** Вернуть топ-N популярных фильмов */
    List<Film> getPopular(int count);
}