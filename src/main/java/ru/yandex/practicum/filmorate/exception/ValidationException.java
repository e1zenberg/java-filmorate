package ru.yandex.practicum.filmorate.exception;

// бросается при ошибке валидации
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}