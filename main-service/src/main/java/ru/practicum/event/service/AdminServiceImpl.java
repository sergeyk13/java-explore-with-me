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
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart,
                                        String rangeEnd, int from, int size) {
        checkState(states);
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from, size, sortById);

        LocalDateTime start = LocalDateTime.parse(rangeStart, FORMATTER);
        LocalDateTime end = LocalDateTime.parse(rangeEnd, FORMATTER);

        Page<Event> eventPage = eventRepository.findAllByInitiatorInAndStateInAndCategoryIdInAndEventDateBetween(users,
                states, categories, start, end, page);
        return MapperPageToList.mapPageToList(eventPage, from, size, EventMapper.INSTANCE::toEventFullDto);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(long eventId, UpdateEventAdminRequest eventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ValidationException("Event not found"));
        EventFullDto eventFullDto;


        if (eventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(eventAdminRequest.getAnnotation());
        }
        if (eventAdminRequest.getCategory() != null) {
            event.setCategory(eventAdminRequest.getCategory());
        }
        if (eventAdminRequest.getDescription() != null) {
            event.setDescription(eventAdminRequest.getDescription());
        }
        try {
            if (eventAdminRequest.getEventDate() != null) {
                event.setEventDate(LocalDateTime.parse(eventAdminRequest.getEventDate(), FORMATTER));
            }
        } catch (DateTimeParseException e) {
            log.error("Error parsing event date", e);
            throw new ValidationException("Unable to parse event date", e.getCause());
        }
        if (eventAdminRequest.getLocation() != null) {
            event.setLocation(eventAdminRequest.getLocation());
        }
        if (eventAdminRequest.getPaid() != null) {
            event.setPaid(eventAdminRequest.getPaid());
        }
        if (eventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventAdminRequest.getParticipantLimit());
        }
        if (eventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(eventAdminRequest.getRequestModeration());
        }
        if (eventAdminRequest.getStateAction() != null) {
            switch (eventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setPublishedOn(LocalDateTime.now());
                    event.setState(State.PUBLISHED);
                    log.info("Event :{} published at: {}", event.getId(), LocalDateTime.now().format(FORMATTER));
                    break;
                case REJECT_EVENT:
                    event.setState(State.CANCELED);
                    log.info("Event :{} at canceled: {}", event.getId(), LocalDateTime.now().format(FORMATTER));
                    break;
            }
        }
        if (eventAdminRequest.getTitle() != null) {
            event.setTitle(eventAdminRequest.getTitle());
        }

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
