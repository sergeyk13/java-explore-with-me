package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.dto.EventShortDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    @Min(0)
    private long id;
    @NotNull
    private boolean pinned;
    @NotBlank
    @Size(min = 3, max = 50, message = "Длина name должна быть от {min} до {max} символов")
    private String title;
    private List<EventShortDto> events = new ArrayList<>();
}
