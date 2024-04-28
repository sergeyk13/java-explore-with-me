package ru.practicum.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.model.ConflictException;
import ru.practicum.error.model.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private RequestRepository requestRepository;
    private UserRepository userRepository;
    private EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        chekcConflictsConflict(userId, event, user);
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);
        if (!event.isRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        } else request.setStatus(Status.PENDING);
        return RequestMapper.INSTANCE.toParticipationRequestDto(requestRepository.save(request));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequest(long userId) {
        User user = checkUser(userId);
        return requestRepository.findAllByRequester(user).stream()
                .map(RequestMapper.INSTANCE::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long requestId, long userId) {
        Request request = checkRequest(requestId);
        User user = checkUser(userId);
        if (request.getRequester().equals(user)) {
            requestRepository.delete(request);
            return RequestMapper.INSTANCE.toParticipationRequestDto(request);
        } else {
            log.error("Request {} trying canceled when user: {} is not participating", requestId, userId);
            throw new ConflictException("Request canceled when user is not participating");
        }
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Request User with id {} not found", userId);
            return new NotFoundException(String.format("Request User with %d not found", userId));
        });
    }

    private Event checkEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Request Event with id {} not found", eventId);
            return new NotFoundException(String.format("Event with id %s not found", eventId));
        });
    }

    private Request checkRequest(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> {
            log.error("Request: {} not found", requestId);
            return new NotFoundException(String.format("Request with id %s not found", requestId));
        });
    }

    private void chekcConflictsConflict(long userId, Event event, User user) {
        if (userId == event.getInitiator().getId()) {
            log.error("Request conflict requester is initiator");
            throw new ConflictException("Request conflict requester is initiator");
        }
        if (requestRepository.findByEventAndRequester(event, user).isPresent()) {
            log.error("Request conflict request is already exist");
            throw new ConflictException("Request conflict request is already exist");
        }
        if (!(event.getState().equals(State.PUBLISHED))) {
            log.error("Request conflict request is not published");
            throw new ConflictException("Request conflict request is not published");
        }
        if (event.getParticipantLimit() != 0 || event.getParticipantLimit() <= requestRepository.countByEvent(event)) {
            log.error("Request conflict participant limit has been reached");
            throw new ConflictException("Request conflict participant limit has been reached");
        }
    }
}
