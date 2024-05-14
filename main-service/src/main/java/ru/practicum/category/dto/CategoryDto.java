package ru.practicum.category.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class CategoryDto {
    @Min(1)
    private long id;
    @NotBlank
    private String name;
}
