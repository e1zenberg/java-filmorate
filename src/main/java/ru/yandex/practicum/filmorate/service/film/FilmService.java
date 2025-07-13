package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmService {

    // добавить фильм
    Film addFilm(Film film);

    // обновить фильм
    Film updateFilm(Film film);

    // получить фильм по id
    Film getFilmById(int id);

    // получить все фильмы
    List<Film> getAllFilms();

    // добавить лайк фильму
    void addLike(int filmId, int userId);

    // убрать лайк с фильма
    void removeLike(int filmId, int userId);

    // получить топ популярных фильмов
    List<Film> getPopular(int count);
}