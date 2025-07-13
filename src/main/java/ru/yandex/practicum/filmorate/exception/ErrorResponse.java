package ru.yandex.practicum.filmorate.exception;

import java.time.Instant;

/**
 * Объект ошибки для API.
 */
public class ErrorResponse {
    private final String error;
    private final String message;
    private final Instant timestamp;

    /**
     * Стандартный конструктор: сразу проставляем текущий timestamp.
     */
    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = Instant.now();
    }

    /**
     * Конструктор с явным указанием timestamp.
     */
    public ErrorResponse(String error, String message, Instant timestamp) {
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
