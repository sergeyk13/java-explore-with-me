package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @Min(1)
    private long id;
    @Size(min = 1, max = 2000, message = "Длина description должна быть от {min} до {max} символов")
    @NotBlank
    private String text;
    @NotNull
    private long authorId;
    @NotNull
    private String createdAt;
}
