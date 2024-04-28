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
import ru.practicum.error.model.ValidationException;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.SortRequest;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.util.MapperPageToList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.util.Constant.APP_NAME;
import static ru.practicum.util.Constant.IP;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicServiceImpl implements PublicService {

    private final EventRepository eventRepository;
    private final Client statssClient;

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, boolean onlyAvailable, String sort, int from, int size) {
        SortRequest.from(sort);
        if (rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Time end befor start");
        }
        Sort sortBy = sort.equals(SortRequest.VIEWS.toString()) ? Sort.by(Sort.Direction.DESC, "views") :
                Sort.by(Sort.Direction.DESC, "event_date");
        Pageable page = PageRequest.of(from, size, sortBy);
        Page<Event> eventPage;

        if (rangeStart == null) {
            eventPage = eventRepository.
                    findAllByStateAndAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidAndEventDateAfterAndAvailable(
                            State.PUBLISHED.toString(), text, categories, paid, LocalDateTime.now(), onlyAvailable, page);
        } else {
            eventPage = eventRepository
                    .findAllByStateAndAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidAndEventDateBetweenAndAvailable(
                            State.PUBLISHED.toString(), text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page);
        }

        List<Event> eventList = MapperPageToList.mapPageToList(eventPage, from, size);
        if (!eventList.isEmpty()) {
            sendAndUpdateHits(eventList);
        }

        return eventList.stream()
                .map(EventMapper.INSTANCE::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventShortDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ValidationException(String.
                format("Event with id: %d not found", eventId)));
        List<Event> eventList = new ArrayList<>(List.of(event));
        sendAndUpdateHits(eventList);
        return EventMapper.INSTANCE.toEventShortDto(event);
    }

    private void sendAndUpdateHits(List<Event> eventList) {
        for (Event event : eventList) {
            String uri = "/events/" + event.getId();
            statssClient.post(new StatisticDto(APP_NAME, uri, IP, LocalDateTime.now().toString()));
            event.setViews(statssClient.get(event.getCreatedOn(), LocalDateTime.now(), new ArrayList<>(List.of(uri)), false).get(0).getHits());
        }
    }
}
