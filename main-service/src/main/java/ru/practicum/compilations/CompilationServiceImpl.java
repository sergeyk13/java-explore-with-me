package ru.practicum.compilations;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.error.model.NotFoundException;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.util.MapperPageToList;
import ru.practicum.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private CompilationRepository compilationRepository;
    private EventRepository eventRepository;


    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.isPinned());
        compilation = compilationRepository.save(compilation);
        log.info("Created new compilation: {}", compilation);

        Compilation finalCompilation = compilation;
        List<Event> events = setCompByEventList(newCompilationDto.getEvents(), finalCompilation);

        return createCompilationDto(finalCompilation, events);
    }

    @Override
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(long compId) {
        compilationRepository.deleteById(compId);
        log.info("Deleted compilation: {}", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = findCompilation(compId);
        List<Event> events = List.of();
        updateCompilationRequest.getTitle().ifPresent(compilation::setTitle);
        updateCompilationRequest.getPinned().ifPresent(compilation::setPinned);
        if (updateCompilationRequest.getEvents().isPresent()) {
            List<Long> eventsId = eventRepository.findIdsByCompilation(compId);
            setCompByEventList(eventsId, null);
            log.info("Updated events: {} for compilation: {}", updateCompilationRequest.getEvents(), compId);
            events = setCompByEventList(updateCompilationRequest.getEvents().get(), compilation);
        }
        ValidationUtil.checkValidation(compilation);
        return createCompilationDto(compilation, events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        List<CompilationDto> listCompilationsDto = new ArrayList<>();
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from, size, sortById);
        Page<Compilation> compilations = compilationRepository.findAllByPinned(pinned, page);
        log.info("Found {} compilations", compilations.getTotalElements());
        List<Compilation> compilationList = MapperPageToList.mapPageToList(compilations, from, size);

        compilationList.forEach(compilation -> {
                    List<Event> events = eventRepository.findAllByCompilation(compilation.getId());
                    listCompilationsDto.add(createCompilationDto(compilation, events));
                }
        );
        return listCompilationsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilation(long compId) {
        Compilation compilation = findCompilation(compId);
        List<Event> events = eventRepository.findAllByCompilation(compId);
        return createCompilationDto(compilation, events);
    }

    private Compilation findCompilation(long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> {
            log.error("Compilation: {} not found", compId);
            return new NotFoundException(String.format("Compilation: %d not found", compId));
        });
    }

    private List<Event> setCompByEventList(List<Long> ids, Compilation finalCompilation) {
        List<Event> events = eventRepository.findAllByIdIn(ids);
        for (Event event : events) {
            event.setCompilation(finalCompilation);
            log.info("Compilation set event: {} compilation: {}", event.getId(), finalCompilation.getId());
        }
        return events;
    }

    private CompilationDto createCompilationDto(Compilation finalCompilation, List<Event> events) {
        CompilationDto compilationDto = CompilationMapper.INSTANCE.toDto(finalCompilation);
        compilationDto.setEvents(events.stream()
                .map(EventMapper.INSTANCE::toEventShortDto)
                .collect(Collectors.toList()));
        log.info("Created new compilationDto: {}", compilationDto);
        return compilationDto;
    }
}
