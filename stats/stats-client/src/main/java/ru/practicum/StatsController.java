package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private final Client client;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatisticDto postHit(@RequestBody @Valid StatisticDto statistic) {
        return client.post("/hit", statistic);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getStats(@RequestParam @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {

        if (start.isAfter(end)) {
            log.error("Start time is after end time");
            throw new ValidationException("Не верно указан запрос дат");
        }
        log.info("Get stats start: {}, end: {}, uri: {}, unique: {}", start, end, uris, unique);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        if (uris != null) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        String urlWithParams = builder.toUriString();
        return client.get(urlWithParams);
    }

}
