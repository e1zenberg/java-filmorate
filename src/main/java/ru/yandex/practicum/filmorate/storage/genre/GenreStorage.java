package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

/**
 * Интерфейс хранилища жанров.
 */
public interface GenreStorage {
    Collection<Genre> getAllGenres();
    Genre getGenreById(int id);
}