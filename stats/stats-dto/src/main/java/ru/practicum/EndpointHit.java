package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHit {
    @Min(1)
    private int id;
    @NotBlank
    private String ip;
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private String timestamp;
}
