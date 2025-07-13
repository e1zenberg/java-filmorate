package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private static final LocalDate EARLIEST_RELEASE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    // добавить фильм
    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        enrichMpaAndGenres(film);
        return filmStorage.addFilm(film);
    }

    // обновить фильм
    @Override
    public Film updateFilm(Film film) {
        int id = (int) film.getId();                   // cast long→int
        // проверить, что фильм существует
        if (filmStorage.getFilmById(id) == null) {
            throw new NotFoundException("Film with id=" + id + " not found");
        }
        validateFilm(film);
        enrichMpaAndGenres(film);
        return filmStorage.updateFilm(film);
    }

    // получить фильм по id
    @Override
    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Film with id=" + id + " not found");
        }
        return film;
    }

    // получить все фильмы
    @Override
    public List<Film> getAllFilms() {
        return List.copyOf(filmStorage.getAllFilms());
    }

    // поставить лайк
    @Override
    public void addLike(int filmId, int userId) {
        getFilmById(filmId);                          // проверка существования
        filmStorage.addLike(filmId, userId);
    }

    // убрать лайк
    @Override
    public void removeLike(int filmId, int userId) {
        getFilmById(filmId);
        filmStorage.removeLike(filmId, userId);
    }

    // топ популярных
    @Override
    public List<Film> getPopular(int count) {
        return List.copyOf(filmStorage.getPopular(count));
    }

    // ---- вспомогательные методы ----

    // валидация полей фильма
    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Description must be up to 200 characters");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(EARLIEST_RELEASE)) {
            throw new ValidationException("Release date must be on or after 1895-12-28");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }

    // загрузка полного MPA и Genres, проверка существования
    private void enrichMpaAndGenres(Film film) {
        int mpaId = film.getMpa().getId();
        Mpa mpa = mpaStorage.getMpaById(mpaId);
        if (mpa == null) {
            throw new NotFoundException("MPA with id=" + mpaId + " not found");
        }
        film.setMpa(mpa);

        Set<Genre> genres = new HashSet<>();
        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                int genreId = g.getId();
                Genre genre = genreStorage.getGenreById(genreId);
                if (genre == null) {
                    throw new NotFoundException("Genre with id=" + genreId + " not found");
                }
                genres.add(genre);
            }
        }
        film.setGenres(genres);
    }
}
