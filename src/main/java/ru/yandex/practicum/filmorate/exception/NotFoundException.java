package ru.yandex.practicum.filmorate.exception;

 // Бросается, когда запрошенный объект не найден в хранилище.
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

