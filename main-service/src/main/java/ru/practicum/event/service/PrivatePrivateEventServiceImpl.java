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
import ru.practicum.error.model.ConflictException;
import ru.practicum.error.model.NotFoundException;
import ru.practicum.error.model.ValidationException;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;
import ru.practicum.util.MapperPageToList;
import ru.practicum.util.ValidationUtil;

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

    private static void checkEventDate(Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException(String.format("Field: eventDate. Error: должно содержать дату, " +
                    "которая еще не наступила. Value: %s", event.getEventDate()));
        }
    }

    @Override
    @Transactional
    public EventFullDto createEvent(long userId, NewEventDto newEventDto) {
        User user = checkUser(userId);
        Category category = checkCategory(newEventDto);
        Event event = EventMapper.INSTANCE.mapToEvent(newEventDto);
        checkEventDate(event);
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
        boolean isCanceled = true;
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        isIterator(user, event);

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
            isCanceled = false;
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(updateEventUserRequest.getCategory());
            isCanceled = false;
        }
        if (updateEventUserRequest.getDescription() != null && !updateEventUserRequest.getDescription().isBlank()) {
            event.setDescription(updateEventUserRequest.getDescription());
            isCanceled = false;
        }
        if (updateEventUserRequest.getEventDate() != null) {
            setEventDate(event, updateEventUserRequest.getEventDate());
            checkEventDate(event);
            isCanceled = false;
        }
        if (updateEventUserRequest.getLocation() != null) {
            Location location = locationRepository.save(updateEventUserRequest.getLocation());
            log.info("Save location: {}", location);
            event.setLocation(location);
            isCanceled = false;
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
            isCanceled = false;
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
            isCanceled = false;
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
            isCanceled = false;
        }
        if (updateEventUserRequest.getState() != null) {
            event.setState(updateEventUserRequest.getState());
            isCanceled = false;
        }
        if (updateEventUserRequest.getTitle() != null && !updateEventUserRequest.getTitle().isBlank()) {
            event.setTitle(updateEventUserRequest.getTitle());
            isCanceled = false;
        }
        if (isCanceled) {
            if (event.getState().equals(State.CANCELED)) {
                event.setState(State.PENDING);
            } else {
                event.setState(State.CANCELED);
            }
        } else event.setState(State.PENDING);

        try {
            EventFullDto eventFullDto = EventMapper.INSTANCE.toEventFullDto(event);
            ValidationUtil.checkValidation(eventFullDto);
            log.info("Updating event: {}", event.getId());
            return eventFullDto;
        } catch (ConstraintViolationException e) {
            log.error("Error parsing event", e);
            throw new ValidationException(e.getMessage(), e.getCause());
        }
    }

    private void setEventDate(Event event, String date) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date, FORMATTER);
            if (dateTime.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Event date must be after 2 hour from now");
            }
            event.setEventDate(dateTime);
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
