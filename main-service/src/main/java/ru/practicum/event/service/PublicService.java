package ru.practicum.event.service;

import ru.practicum.event.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicService {

    List<EventShortDto> getEvents(String text, List<Long> categories, boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                  boolean onlyAvailable, String sort, int from, int size);

    EventShortDto getEvent(long eventId);
}
