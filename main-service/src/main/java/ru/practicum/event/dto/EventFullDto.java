package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private long id;
    @Size(min = 20, max = 2000, message = "Длина annotation должна быть от {min} до {max} символов")
    private String annotation;
    @NotNull
    private Category category;
    @Min(0)
    private long confirmedRequests;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",
            message = "Формат даты и времени должен быть 'год-месяц-день час:минута:секунда'")
    private String createdOn;
    @Size(min = 20, max = 7000, message = "Длина description должна быть от {min} до {max} символов")
    private String description;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",
            message = "Формат даты и времени должен быть 'год-месяц-день час:минута:секунда'")
    private String eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Location location;
    @NotNull
    private boolean paid;
    @Min(0)
    private long participantLimit;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",
            message = "Формат даты и времени должен быть 'год-месяц-день час:минута:секунда'")
    private String publishedOn;
    private boolean requestModeration;
    private State state;
    @Size(min = 3, max = 120, message = "Длина title должна быть от {min} до {max} символов")
    private String title;
    @Min(0)
    private long views;
}
