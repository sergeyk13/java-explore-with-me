package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.model.ValidationException;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.util.MapperPageToList;
import ru.practicum.util.Validation;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static ru.practicum.util.Constant.FORMATTER;

@Service
@AllArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rengeStart,
                                        String rengeEnd, int from, int size) {
        checkState(states);
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from, size, sortById);

        LocalDateTime start = LocalDateTime.parse(rengeStart, FORMATTER);
        LocalDateTime end = LocalDateTime.parse(rengeEnd, FORMATTER);

        Page<Event> eventPage = eventRepository.findAllByInitiatorInAndStateInAndCategoryIdInAndEventDateBetween(users,
                states, categories, start, end, page);
        return MapperPageToList.mapPageToList(eventPage, from, size, EventMapper.INSTANCE::toEventFullDto);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(long eventId, UpdateEventAdminRequest eventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ValidationException("Event not found"));
        EventFullDto eventFullDto;
        eventAdminRequest.getAnnotation().ifPresent(event::setAnnotation);
        eventAdminRequest.getCategory().ifPresent(event::setCategory);
        eventAdminRequest.getDescription().ifPresent(event::setDescription);
        try {
            eventAdminRequest.getEventDate().ifPresent(eventDate -> event.setEventDate(LocalDateTime.parse(eventDate, FORMATTER)));
        } catch (DateTimeParseException e) {
            log.error("Error parsing event date", e);
            throw new ValidationException("Unable to parse event date", e.getCause());
        }
        eventAdminRequest.getLocation().ifPresent(event::setLocation);
        eventAdminRequest.getPaid().ifPresent(event::setPaid);
        eventAdminRequest.getParticipantLimit().ifPresent(event::setParticipantLimit);
        eventAdminRequest.getRequestModeration().ifPresent(event::setRequestModeration);
        eventAdminRequest.getState().ifPresent(event::setState);
        eventAdminRequest.getTitle().ifPresent(event::setTitle);
        try {
            eventFullDto = EventMapper.INSTANCE.toEventFullDto(event);
            Validation.checkValidation(eventFullDto);
            log.info("Update event: {}", event);
            return eventFullDto;
        } catch (ConstraintViolationException e) {
            log.error("Error parsing event", e);
            throw new ValidationException(e.getMessage(), e.getCause());
        }
    }

    private void checkState(List<String> states) {
        states.forEach(state -> State.from(state).orElseThrow(() -> new ValidationException("Invalid state")));
    }
}
