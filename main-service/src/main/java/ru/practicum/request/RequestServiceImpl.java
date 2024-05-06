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
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
        checkConflictsConflict(event.getInitiator().getId(), event, user);
        Request request = new Request();
        request.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        request.setEvent(event);
        request.setRequester(user);
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(Status.CONFIRMED);
        } else request.setStatus(Status.PENDING);
        request = requestRepository.save(request);
        log.info("Create request: {}", request);
        return RequestMapper.INSTANCE.toParticipationRequestDto(request);
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
            request.setStatus(Status.CANCELED);
            requestRepository.delete(request);
            log.info("Canceled request: {}", request);
            return RequestMapper.INSTANCE.toParticipationRequestDto(request);
        } else {
            log.error("Request {} trying canceled when user: {} is not participating", requestId, userId);
            throw new ConflictException("Request canceled when user is not participating");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        User initiator = checkUser(userId);
        Event event = checkEvent(eventId);
        Request request = requestRepository.findByEventId(eventId).orElseThrow(() -> {
            log.error("Request for user: {} and event: {} not found", userId, eventId);
            return new NotFoundException(String.format("Request for user: %d and event: %d not found", userId, eventId));
        });
        log.info("Return request {}", request);
        return List.of(RequestMapper.INSTANCE.toParticipationRequestDto(request));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchStatusRequests(long userId, long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        checkInitiator(userId, event);
        List<Request> requestList = requestRepository.findAllByEventAndStatus(event, Status.PENDING);
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (Request request : requestList) {
            if (updateRequest.getRequestIds().contains(request.getId())) {
                if (updateRequest.getStatus().equals(Status.CONFIRMED)) {
                    request.setStatus(updateRequest.getStatus());
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);

                    if (event.getConfirmedRequests() == event.getParticipantLimit()) {
                        event.setAvailable(false);
                    }
                    confirmedRequests.add(RequestMapper.INSTANCE.toParticipationRequestDto(request));
                } else if (updateRequest.getStatus().equals(Status.REJECTED)) {
                    request.setStatus(Status.REJECTED);
                    rejectedRequests.add(RequestMapper.INSTANCE.toParticipationRequestDto(request));
                }
                log.info("Set status: {} for request: {}", updateRequest.getStatus(), request);
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
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

    private void checkConflictsConflict(long initiatorId, Event event, User requester) {
        checkInitiator(initiatorId, event);
        if (requestRepository.findByEventIdAndRequesterId(event.getId(), requester.getId()).isPresent()) {
            log.error("Request conflict request is already exist");
            throw new ConflictException("Request conflict request is already exist");
        }
        if (!(event.getState().equals(State.PUBLISHED))) {
            log.error("Request conflict request is not published");
            throw new ConflictException("Request conflict request is not published");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            log.error("Request conflict participant limit: {} has been reached: {} ", event.getParticipantLimit(),
                    event.getConfirmedRequests());
            throw new ConflictException("Request conflict participant limit has been reached");
        }
    }

    private static void checkInitiator(long initiatorId, Event event) {
        if (initiatorId != event.getInitiator().getId()) {
            log.error("Request conflict requester is initiator");
            throw new ConflictException("Request conflict requester is initiator");
        }
    }
}
