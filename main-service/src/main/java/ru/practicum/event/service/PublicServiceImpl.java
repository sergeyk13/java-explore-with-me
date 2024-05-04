package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.Client;
import ru.practicum.StatisticDto;
import ru.practicum.ViewStats;
import ru.practicum.error.model.NotFoundException;
import ru.practicum.error.model.ValidationException;
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

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, boolean onlyAvailable, String sort, int from, int size) {
        Optional<SortRequest> sortRequest = SortRequest.from(sort);
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Time end before start");
        }
        Sort sortBy;
        sortBy = sortRequest.map(request -> request == SortRequest.VIEWS ? Sort.by(Sort.Direction.DESC, "views") :
                Sort.by(Sort.Direction.DESC, "event_date")).orElseGet(() -> Sort.by(Sort.Direction.DESC, "id"));
        Pageable page = PageRequest.of(from, size, sortBy);
        Page<Event> eventPage;

        if ((text == null || text.isEmpty()) && (categories == null || categories.isEmpty())) {
            if (rangeStart == null) {
                eventPage = eventRepository.findAllByPaidAndEventDateAfter(paid, LocalDateTime.now(), page);
            } else
                eventPage = eventRepository.findAllByPaidAndEventDateBetween(paid, rangeStart, rangeEnd, page);
        } else if (rangeStart == null) {
            eventPage = eventRepository.
                    findAllByStateAndAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidAndEventDateAfterAndAvailable(
                            State.PUBLISHED, text, categories, paid, LocalDateTime.now(), onlyAvailable, page);
        } else {
            eventPage = eventRepository
                    .findAllByStateAndAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidAndEventDateBetweenAndAvailable(
                            State.PUBLISHED, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page);
        }

        List<Event> eventList = MapperPageToList.mapPageToList(eventPage, from, size);
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
    public EventShortDto getEvent(long eventId) {
        Event event = eventRepository.findByIdAndState(eventId,State.PUBLISHED).orElseThrow(() -> new NotFoundException(String.
                format("Event with id: %d not found", eventId)));
        List<Event> eventList = new ArrayList<>(List.of(event));
        sendHits(eventList);
        Map<Long,Long>  eventViews = getViews(eventList,event.getCreatedOn(),event.getEventDate());
        for (Event e : eventList) {
            if (eventViews.containsKey(eventId)) {
                e.setViews(eventViews.get(eventId));
            }
        }
        return EventMapper.INSTANCE.toEventShortDto(event);
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

        List<ViewStats> viewStatsList = statssClient.get(rangeStart, rangeEnd, uris, false);

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
