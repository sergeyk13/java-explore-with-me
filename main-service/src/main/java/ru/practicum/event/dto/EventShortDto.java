package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private long id;
    @Size(min = 20, max = 2000, message = "Длина annotation должна быть от {min} до {max} символов")
    private String annotation;
    @NotNull
    private Category category;
    @Min(0)
    private long confirmedRequests;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",
            message = "Формат даты и времени должен быть 'год-месяц-день час:минута:секунда'")
    private String eventDate;
    @NotNull
    private UserShortDto initiator;
    private boolean paid;
    @Size(min = 3, max = 120, message = "Длина title должна быть от {min} до {max} символов")
    private String title;
    @Min(0)
    private long views;
}
