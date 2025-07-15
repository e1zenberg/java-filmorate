package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

/**
 * Интерфейс хранилища жанров.
 */
public interface GenreStorage {

    /**
     * Получить всю коллекцию жанров.
     *
     * @return коллекция жанров
     */
    Collection<Genre> getAllGenres();

    /**
     * Получить жанр по идентификатору.
     *
     * @param id идентификатор жанра
     * @return объект жанра
     */
    Genre getGenreById(int id);

}