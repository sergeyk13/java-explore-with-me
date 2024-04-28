package ru.practicum.event.model;

import java.util.Optional;

public enum SortRequest {
    EVENT_DATE,
    VIEWS;

    public static Optional<SortRequest> from(String s) {
        for (SortRequest sort : values()) {
            if (sort.name().equalsIgnoreCase(s)) {
                return Optional.of(sort);
            }
        }
        return Optional.empty();
    }
}
