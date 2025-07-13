package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Collection;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() <= 0) {
            throw new ValidationException("ID фильма должно быть положительным");
        }
        // проверяем, что фильм есть
        filmStorage.getFilmById((int) film.getId());
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    /** Ставит лайк */
    public void addLike(int filmId, int userId) {
        // можно добавить проверку существования пользователя через UserStorage
        filmStorage.addLike(filmId, userId);
    }

    /** Убирает лайк */
    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(filmId, userId);
    }

    /** Возвращает топ-N популярных фильмов */
    public List<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 1000) {
            throw new ValidationException("Описание не должно превышать 1000 символов");
        }
        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
        if (film.getMpa() == null) {
            throw new ValidationException("Рейтинг MPA должен быть указан");
        }
    }
}
