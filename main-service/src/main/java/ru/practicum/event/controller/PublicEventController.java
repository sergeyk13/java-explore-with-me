package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.PublicService;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.util.Constant.DATE_FORMAT;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
@Slf4j
public class PublicEventController {
    private final PublicService publicService;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false, defaultValue = "false") boolean paid,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
                                         @RequestParam(required = false, defaultValue = "false") boolean onlyAvailable,
                                         @RequestParam(required = false, defaultValue = "") String sort,
                                         @RequestParam(required = false, name = "from", defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(required = false, name = "size", defaultValue = "10") @PositiveOrZero int size) {
        log.info("getEvents called");
        return publicService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    public EventShortDto getEvent(@PathVariable("eventId") long eventId) {
        log.info("getEvent called");
        return publicService.getEvent(eventId);
    }

}
