package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {
    @Email
    @NotBlank
    @Size(min = 6, max = 254, message = "Длина должны быть не менее 6 символов и не более 254")
    private String email;
    @NotBlank
    @Size(min = 2, max = 250, message = "Длина должны быть не менее 2 символов и не более 250")
    private String name;
}
