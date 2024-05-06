package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.Client;
import ru.practicum.StatisticDto;
import ru.practicum.ViewStats;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.error.model.NotFoundException;
import ru.practicum.event.EventSpecifications;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.SortRequest;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.util.MapperPageToList;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.util.Constant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicServiceImpl implements PublicService {

    private final EventRepository eventRepository;
    private final Client statssClient;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from, int size) {
        Specification<Event> spec = Specification.where(null);
        List<Category> categoriesList;
        Sort sortBy;
        Optional<SortRequest> sortRequest = SortRequest.from(sort);

        sortBy = sortRequest.map(request -> request == SortRequest.VIEWS ? Sort.by(Sort.Direction.DESC, "views") :
                Sort.by(Sort.Direction.DESC, "eventDate")).orElseGet(() -> Sort.by(Sort.Direction.DESC, "id"));
        Pageable page = PageRequest.of(from, size, sortBy);
        Page<Event> eventPage;

        if (text != null && !text.isEmpty()) {
            spec = spec.and(EventSpecifications.byAnnotation(text));
        }
        if (categories != null && !categories.isEmpty()) {
            categoriesList = categories.stream().map(category -> categoryRepository.findById(category)
                    .orElseThrow(() -> new NotFoundException("Category not found"))).collect(Collectors.toList());
            spec = spec.and(EventSpecifications.byCategoryIdIn(categoriesList));
        }
        if (paid != null) {
            spec = spec.and(EventSpecifications.byPaid(paid));
        }
        if (onlyAvailable != null && onlyAvailable) {
            spec = spec.and(EventSpecifications.byAvailable(true));
        }

        eventPage = eventRepository.findAll(spec, page);
        List<Event> eventList = MapperPageToList.mapPageToList(eventPage, from, size);
        eventList = checkIsAvailable(eventList);
        if (onlyAvailable != null) {
            eventList = checkIsAvailable(eventList);
        }
        if (!eventList.isEmpty()) {
            sendHits(eventList);
            Map<Long,Long>  eventViews = getViews(eventList,rangeStart,rangeEnd);
            for (Event event : eventList) {
                Long eventId = event.getId();
                if (eventViews.containsKey(eventId)) {
                    event.setViews(eventViews.get(eventId));
                }
            }
        }

        return eventList.stream()
                .map(EventMapper.INSTANCE::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent(long eventId) {
        Event event = eventRepository.findByIdAndState(eventId,State.PUBLISHED).orElseThrow(() -> new NotFoundException(String.
                format("Event with id: %d not found", eventId)));
        List<Event> eventList = new ArrayList<>(List.of(event));
        eventList = checkIsAvailable(eventList);
        sendHits(eventList);
        Map<Long,Long>  eventViews = getViews(eventList,event.getCreatedOn(),event.getEventDate());
        for (Event e : eventList) {
            if (eventViews.containsKey(eventId)) {
                e.setViews(eventViews.get(eventId));
            }
        }
        return EventMapper.INSTANCE.toEventFullDto(event);
    }

    private static List<Event> checkIsAvailable(List<Event> eventList) {
        return eventList.stream()
                .peek(event -> {
                    if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                        event.setAvailable(false);
                    }
                })
                .collect(Collectors.toList());
    }

    private void sendHits(List<Event> eventList) {
        for (Event event : eventList) {
            String uri = "/events/" + event.getId();
            LocalDateTime time = LocalDateTime.now();
            log.info("Sending hit for event: {} to stats server", event.getId());
            statssClient.post(new StatisticDto(APP_NAME, uri, IP, time.format(FORMATTER)));
        }

    }

    private Map<Long, Long> getViews(List<Event> eventList, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        List<String> uris = new ArrayList<>();
        for (Event event : eventList) {
            uris.add("/events/" + event.getId());
        }
        log.info("Getting views for events: {}", uris);
        if (rangeStart == null || rangeEnd == null) {
            rangeStart=LocalDateTime.now().minusMinutes(1);
            rangeEnd=LocalDateTime.now().plusYears(1);
        }
        List<ViewStats> viewStatsList = statssClient.get(rangeStart, rangeEnd, uris, true);

        Map<Long, Long> eventViews = new HashMap<>();
        for (ViewStats viewStats : viewStatsList) {
            String[] parts = viewStats.getUri().split("/");
            Long key = Long.parseLong(parts[parts.length - 1]);
            Long value = viewStats.getHits();
            eventViews.put(key, value);
        }
        return eventViews;
    }
}
