package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

/**
 * Интерфейс хранилища рейтингов MPA.
 */
public interface MpaStorage {
    Collection<Mpa> getAllMpa();
    Mpa getMpaById(int id);
}
