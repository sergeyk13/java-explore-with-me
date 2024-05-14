package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @Min(1)
    private long id;
    @Email
    @NotEmpty
    private String email;
    @NotBlank
    @Size(min = 6)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Должны быть только символы")
    private String name;
}
