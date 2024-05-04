package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.error.model.NotFoundException;
import ru.practicum.error.model.ValidationException;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;
import ru.practicum.util.MapperPageToList;
import ru.practicum.util.Validation;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static ru.practicum.util.Constant.FORMATTER;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivatePrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public EventFullDto createEvent(long userId, NewEventDto newEventDto) {
        User user = checkUser(userId);
        Category category = checkCategory(newEventDto);
        Event event = EventMapper.INSTANCE.mapToEvent(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(State.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setViews(0);
        locationRepository.save(newEventDto.getLocation());
        log.info("Save location: {}", newEventDto.getLocation());
        event = eventRepository.save(event);
        log.info("Created new event: {}", event.getId());
        return EventMapper.INSTANCE.toEventFullDto(event);
    }


    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(long userId, int from, int size) {
        User user = checkUser(userId);
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from, size, sortById);
        Page<Event> eventPage = eventRepository.findAllByInitiator(user, page);
        log.info("Get events by userId: {},", userId);
        return MapperPageToList.mapPageToList(eventPage, from, size, EventMapper.INSTANCE::toEventShortDto);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto findEvent(long userId, long eventId) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        isIterator(user, event);
        log.info("Get event: {}", event);
        return EventMapper.INSTANCE.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        isIterator(user, event);

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(updateEventUserRequest.getCategory());
        }
        if (updateEventUserRequest.getConfirmedRequests() != null) {
            event.setConfirmedRequests(updateEventUserRequest.getConfirmedRequests());
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            setEventDate(event, updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(updateEventUserRequest.getLocation());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getState() != null) {
            event.setState(updateEventUserRequest.getState());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        try {
            EventFullDto eventFullDto = EventMapper.INSTANCE.toEventFullDto(event);
            Validation.checkValidation(eventFullDto);
            log.info("Updating event: {}", event.getId());
            return eventFullDto;
        } catch (ConstraintViolationException e) {
            log.error("Error parsing event", e);
            throw new ValidationException(e.getMessage(), e.getCause());
        }
    }

    private void setEventDate(Event event, String date) {
        try {
            event.setEventDate(LocalDateTime.parse(date, FORMATTER));
        } catch (DateTimeParseException e) {
            log.error("Error parsing event date", e);
            throw new ValidationException("Unable to parse event date", e.getCause());
        }
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with id: {} not found", userId);
            return new NotFoundException("User with id " + userId + " not found");
        });
    }

    private Category checkCategory(NewEventDto newEventDto) {
        return categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> {
            log.error("Category with id: {} not found", newEventDto.getCategory());
            return new NotFoundException("Category with id " + newEventDto.getCategory() + " not found");
        });
    }

    private Event checkEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with id: {} not found", eventId);
            return new NotFoundException("Event with id " + eventId + " not found");
        });
    }

    private void isIterator(User user, Event event) {
        if (!event.getInitiator().equals(user)) {
            log.error("User:{} not initiator for event:{}", user.getId(), event.getId());
            throw new NotFoundException("Event where user: " + user.getId() + " not found");
        }
    }
}
