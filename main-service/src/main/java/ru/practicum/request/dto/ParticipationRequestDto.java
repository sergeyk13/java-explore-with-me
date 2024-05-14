package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.request.model.Status;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    private long id;
    @NotNull
    private long event;
    @NotBlank
    private String created;
    @NotNull
    private long requester;
    @NotNull
    private Status status;
}
