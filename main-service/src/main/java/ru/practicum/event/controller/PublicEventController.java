package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventShortDto;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.util.Constant.DATE_FORMAT;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
@Slf4j
public class PublicEventController {

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) boolean paid,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) String rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) String rangeEnd,
                                         @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        return null;
    }

    @GetMapping("/{eventId}")
    public EventShortDto getEvent(@PathVariable("eventId") long eventId) {
        return null;
    }

}
