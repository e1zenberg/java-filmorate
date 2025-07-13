package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

/**
 * Интерфейс хранилища рейтингов MPA.
 */
public interface MpaStorage {

    /**
     * Получить все рейтинги MPA.
     *
     * @return коллекция рейтингов
     */
    Collection<Mpa> getAllMpa();

    /**
     * Получить рейтинг MPA по идентификатору.
     *
     * @param id идентификатор рейтинга
     * @return объект MPA
     */
    Mpa getMpaById(int id);

}