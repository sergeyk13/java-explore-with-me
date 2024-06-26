package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constant.StringConstant.FORMAT;

@RestController
@AllArgsConstructor
@Slf4j
@Validated

public class StatsController {
    private final StatsServiceImpl service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatisticDto postHit(@RequestBody @Valid StatisticDto statisticDto) {
        log.info("POST hit statistic: {}", statisticDto);
        return service.postHit(statisticDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = FORMAT) LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = FORMAT) LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Get stats start: {}, end: {}, uri: {}, unique: {}", start, end, uris, unique);
        return service.getStats(start, end, uris, unique);
    }
}
