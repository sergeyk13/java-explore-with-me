package ru.practicum.request.model;

import java.util.Optional;

public enum Status {
    PENDING,
    CONFIRMED,
    CANCELED,
    REJECTED;

    public static Optional<Status> from(String stringState) {
        for (Status status : values()) {
            if (status.name().equalsIgnoreCase(stringState)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
