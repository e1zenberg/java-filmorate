package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервисная реализация для работы с фильмами.
 */
@Service
public class FilmServiceImpl implements FilmService {
    // Хранилище фильмов (например, JDBC, в памяти и т.п.)
    private final FilmStorage filmStorage;

    /**
     * Внедрение FilmStorage через конструктор.
     */
    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    /**
     * Добавляет новый фильм в хранилище после валидации.
     */
    @Override
    public Film addFilm(Film film) {
        validateFilm(film);                     // проверяем поля
        return filmStorage.addFilm(film);       // сохраняем и возвращаем
    }

    /**
     * Обновляет существующий фильм.
     * Если фильм не найден — бросает NotFoundException.
     */
    @Override
    public Film updateFilm(Film film) {
        int id = (int) film.getId();            // приводим long в int
        Film existing = filmStorage.getFilmById(id);
        if (existing == null) {
            throw new NotFoundException("Film with id=" + id + " not found");
        }
        validateFilm(film);                     // проверяем поля
        return filmStorage.updateFilm(film);    // обновляем и возвращаем
    }

    /**
     * Возвращает фильм по ID или бросает NotFoundException.
     */
    @Override
    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Film with id=" + id + " not found");
        }
        return film;
    }

    /**
     * Возвращает список всех фильмов.
     */
    @Override
    public List<Film> getAllFilms() {
        return List.copyOf(filmStorage.getAllFilms());
    }

    /**
     * Возвращает список популярных фильмов,
     * ограниченный count штук.
     */
    @Override
    public List<Film> getPopular(int count) {
        return List.copyOf(filmStorage.getPopular(count));
    }

    /**
     * Добавляет запись о лайке фильма пользователем.
     */
    @Override
    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    /**
     * Удаляет лайк фильма от пользователя.
     */
    @Override
    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(filmId, userId);
    }

    /**
     * Валидация полей фильма:
     * - имя не пустое,
     * - описание ≤200 символов,
     * - дата выхода ≥28.12.1895,
     * - длительность >0.
     */
    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null
                && film.getDescription().length() > 200) {
            throw new ValidationException("Description must be up to 200 characters");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Release date must be on or after 1895-12-28");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }
}
