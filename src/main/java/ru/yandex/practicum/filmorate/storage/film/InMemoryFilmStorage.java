package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

//Реализация FilmStorage на основе in-memory HashMap.

@Component  // Позволяет Spring найти и внедрить это хранилище
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final AtomicInteger idGen = new AtomicInteger(0);  // Генератор уникальных ID

    // Первая дата показа фильма в истории
    private static final LocalDate FIRST_CINEMA_DATE =
            LocalDate.of(1895, Month.DECEMBER, 28);

    @Override
    public Film addFilm(Film film) {
        validate(film);  // Проверяем корректность всех полей фильма перед сохранением в хранилище
        int id = idGen.incrementAndGet();  // Получаем следующий уникальный идентификатор для фильма
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validate(film);  // Проверяем корректность всех полей фильма перед обновлением существующей записи
        int id = film.getId(); // Извлекаем ID фильма для поиска
        if (!films.containsKey(id)) {
            throw new ValidationException("Film with id=" + id + " not found");
        }
        films.put(id, film); // Обновляем данные фильма по ключу id
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new ValidationException("Film with id=" + id + " not found");
        }
        return film;
    }

    // Вспомогательный метод для валидации полей Film
    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Name must not be empty");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Description must be <=200 characters");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(FIRST_CINEMA_DATE)) {
            throw new ValidationException("Release date must be after 1895-12-28");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Duration must be positive");
        }
    }
}
