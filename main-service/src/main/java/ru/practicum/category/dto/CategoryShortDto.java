package ru.practicum.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategoryShortDto {
    @NotBlank
    @Size(min = 3, max = 50, message = "Длина name должна быть от {min} до {max} символов")
    private String name;
}
