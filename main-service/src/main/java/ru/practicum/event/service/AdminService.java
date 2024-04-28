package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;

import java.util.List;

public interface AdminService {
    List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories,
                                 String rengeStart, String rengeEnd, int from, int size);

    EventFullDto updateEvent(long eventId, UpdateEventAdminRequest eventAdminRequest);
}
