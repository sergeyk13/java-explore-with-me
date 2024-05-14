package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Location;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @Size(min = 20, max = 2000, message = "Длина annotation должна быть от {min} до {max} символов")
    private String annotation;
    @NotNull
    private long category;
    @Size(min = 20, max = 7000, message = "Длина description должна быть от {min} до {max} символов")
    private String description;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",
            message = "Формат даты и времени должен быть 'год-месяц-день час:минута:секунда'")
    private String eventDate;
    @NotNull
    private Location location;
    private boolean paid = false;
    @Min(0)
    private long participantLimit = 0;
    private boolean requestModeration = true;
    @Size(min = 3, max = 120, message = "Длина title должна быть от {min} до {max} символов")
    private String title;
}
