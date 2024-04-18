package ru.practicum.error.model;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

