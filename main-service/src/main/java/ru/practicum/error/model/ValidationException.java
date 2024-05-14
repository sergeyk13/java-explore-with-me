package ru.practicum.error.model;

import lombok.Data;

@Data
public class ValidationException extends RuntimeException {
    private Throwable cause;

    public ValidationException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public ValidationException(String message) {
        super(message);
    }
}

