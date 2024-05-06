package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.error.model.NotFoundException;
import ru.practicum.error.model.ValidationException;
import ru.practicum.event.EventSpecifications;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
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
import java.util.stream.Collectors;

import static ru.practicum.util.Constant.FORMATTER;

@Service
@AllArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private LocationRepository locationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart,
                                        String rangeEnd, int from, int size) {

        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from, size, sortById);
        Page<Event> eventPage;
        List<State> stateList;
        List<User> userList;
        List<Category> categoriesList;
        LocalDateTime start;
        LocalDateTime end;
        Specification<Event> spec = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            userList = users.stream().map(user -> userRepository.findById(user).orElseThrow(() ->
                    new NotFoundException("User not found"))).collect(Collectors.toList());
            spec = spec.and(EventSpecifications.byInitiatorIn(userList));
        }
        if (states != null && !states.isEmpty()) {
            checkState(states);
            stateList = states.stream().map(State::valueOf).collect(Collectors.toList());
            spec = spec.and(EventSpecifications.byStateIn(stateList));
        }
        if (categories != null && !categories.isEmpty()) {
            categoriesList = categories.stream().map(category -> categoryRepository.findById(category)
                    .orElseThrow(() -> new NotFoundException("Category not found"))).collect(Collectors.toList());
            spec = spec.and(EventSpecifications.byCategoryIdIn(categoriesList));
        }

        if (rangeStart != null && !rangeStart.isBlank() && rangeEnd != null && !rangeEnd.isBlank()) {
            try {
                start = LocalDateTime.parse(rangeStart, FORMATTER);
                end = LocalDateTime.parse(rangeEnd, FORMATTER);
                spec = spec.and(EventSpecifications.byEventDateBetween(start, end));
            } catch (DateTimeParseException e) {
                throw new ValidationException("Date format error");
            }
        }
        eventPage = eventRepository.findAll(spec, page);
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
            Category category = categoryRepository.findById(eventAdminRequest.getCategory()).orElseThrow(() -> {
                log.info("Category witch id: {} not found", eventAdminRequest.getCategory());
                return new NotFoundException(String.format("Category witch id: %d not found",
                        eventAdminRequest.getCategory()));
            });
            event.setCategory(category);
        }
        if (eventAdminRequest.getDescription() != null) {
            event.setDescription(eventAdminRequest.getDescription());
        }
        try {
            if (eventAdminRequest.getEventDate() != null) {
                event.setEventDate(LocalDateTime.parse(eventAdminRequest.getEventDate(), FORMATTER));
                checkEventDate(event);
            }
        } catch (DateTimeParseException e) {
            log.error("Error parsing event date", e);
            throw new ValidationException("Unable to parse event date", e.getCause());
        }
        if (eventAdminRequest.getLocation() != null) {
            Location location = locationRepository.save(eventAdminRequest.getLocation());
            log.info("Save location: {}", location);
            event.setLocation(location);
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
            ValidationUtil.checkValidation(eventFullDto);
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

    private static void checkEventDate(Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException(String.format("Field: eventDate. Error: должно содержать дату, " +
                    "которая еще не наступила. Value: %s", event.getEventDate()));
        }
    }
}
