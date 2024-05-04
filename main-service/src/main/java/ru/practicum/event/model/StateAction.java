package ru.practicum.event.model;

import java.util.Optional;

public enum StateAction {
    PUBLISH_EVENT,
    REJECT_EVENT;

    public static Optional<StateAction> from(String stringState) {
        for (StateAction state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
