package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage,
                           MpaStorage mpaStorage,
                           GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);

        Mpa mpa = mpaStorage.getMpaById(film.getMpa().getId());
        if (mpa == null) {
            throw new NotFoundException("MPA id=" + film.getMpa().getId() + " not found");
        }
        film.setMpa(mpa);

        Set<Genre> genres = film.getGenres() == null
                ? Set.of()
                : film.getGenres().stream()
                .map(g -> {
                    Genre found = genreStorage.getGenreById(g.getId());
                    if (found == null) {
                        throw new NotFoundException("Genre id=" + g.getId() + " not found");
                    }
                    return found;
                })
                .collect(Collectors.toSet());
        film.setGenres(genres);

        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        int id = (int) film.getId();
        if (filmStorage.getFilmById(id) == null) {
            throw new NotFoundException("Film id=" + film.getId() + " not found");
        }
        validateFilm(film);

        Mpa mpa = mpaStorage.getMpaById(film.getMpa().getId());
        if (mpa == null) {
            throw new NotFoundException("MPA id=" + film.getMpa().getId() + " not found");
        }
        film.setMpa(mpa);

        Set<Genre> genres = film.getGenres() == null
                ? Set.of()
                : film.getGenres().stream()
                .map(g -> {
                    Genre found = genreStorage.getGenreById(g.getId());
                    if (found == null) {
                        throw new NotFoundException("Genre id=" + g.getId() + " not found");
                    }
                    return found;
                })
                .collect(Collectors.toSet());
        film.setGenres(genres);

        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.getAllFilms());
    }

    @Override
    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Film id=" + id + " not found");
        }
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        return new ArrayList<>(filmStorage.getPopular(count));
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Name must not be blank");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Description must be up to 200 characters");
        }
        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Release date must be on or after 1895-12-28");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Duration must be positive");
        }
    }
}
