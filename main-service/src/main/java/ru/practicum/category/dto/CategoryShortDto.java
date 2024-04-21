package ru.practicum.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryShortDto {
    @NotBlank
    private String name;
}
