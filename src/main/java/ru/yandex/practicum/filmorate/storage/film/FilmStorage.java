package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

/**
 * Интерфейс для хранилища фильмов.
 * Определяет базовые CRUD-операции.
 */
public interface FilmStorage {

    /**
     * Сохраняет новый фильм в хранилище.
     * @param film объект фильма без id
     * @return тот же объект, но с присвоенным id
     */
    Film addFilm(Film film);

    /**
     * Обновляет данные существующего фильма.
     * @param film объект фильма с уже существующим id
     * @return обновлённый объект
     */
    Film updateFilm(Film film);

    /**
     * Возвращает список всех фильмов.
     */
    Collection<Film> getAllFilms();

    /**
     * Возвращает фильм по его id.
     * @param id идентификатор фильма
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если фильм не найден
     */
    Film getFilmById(int id);
}