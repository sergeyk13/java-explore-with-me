package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@AllArgsConstructor
@Slf4j
public class AdminController {

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) final List<Long> users,
                                        @RequestParam(required = false) final List<String> states,
                                        @RequestParam(required = false) final List<Long> categories,
                                        @RequestParam(required = false) String rengeStart,
                                        @RequestParam(required = false) String rengeEnd,
                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        return null;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable("eventId") long eventId,
                                    @RequestBody UpdateEventAdminRequest eventAdminRequest) {
        return null;
    }
}
