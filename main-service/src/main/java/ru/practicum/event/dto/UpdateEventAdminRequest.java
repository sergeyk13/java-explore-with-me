package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {
    private Optional<String> annotation;
    private Optional<Category> category;
    private Optional<String> description;
    private Optional<String> eventDate;
    private Optional<Location> location;
    private Optional<Boolean> paid;
    private Optional<Long> participantLimit;
    private Optional<Boolean> requestModeration;
    private Optional<State> state;
    private Optional<String> title;
}
