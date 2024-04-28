package ru.practicum.request;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;


public interface RequestService {
    ParticipationRequestDto createRequest(long userId, long eventId);

    List<ParticipationRequestDto> getRequest(long userId);

    ParticipationRequestDto cancelRequest(long requestId, long userId);
}
