package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.PrivateEventService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/")
@AllArgsConstructor
@Validated
@Slf4j
public class PrivateEventController {

    private final PrivateEventService privateEventService;

    @PostMapping("{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Post new event from userId: {}", userId);
        return privateEventService.createEvent(userId, newEventDto);
    }

    @GetMapping("{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@PathVariable("userId") long userId,
                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        log.info("Get events by page from:{} , size{}, from user {}", from, size, userId);
        return privateEventService.getEvents(userId, from, size);
    }

    @GetMapping("{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findEvent(@PathVariable("userId") long userId, @PathVariable("eventId") long eventId) {
        log.info("Get event by id from user {} with event {}", userId, eventId);
        return privateEventService.findEvent(userId, eventId);
    }

    @PatchMapping("{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable("userId") long userId,
                                    @PathVariable("eventId") long eventId,
                                    @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Update event by id from user {} with event {}", userId, eventId);
        return privateEventService.updateEvent(userId, eventId, updateEventUserRequest);
    }
}
