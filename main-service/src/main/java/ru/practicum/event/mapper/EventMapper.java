package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.UserMapper;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static ru.practicum.util.Constant.FORMATTER;

@Mapper(uses = UserMapper.class)
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventDate", source = "eventDate", qualifiedByName = "mapStringToDate")
    Event mapToEvent(NewEventDto newEventDto);

    @Mapping(source = "initiator", target = "initiator")
    @Mapping(target = "createdOn", source = "createdOn", qualifiedByName = "mapDateToString")
    @Mapping(target = "eventDate", source = "eventDate", qualifiedByName = "mapDateToString")
    @Mapping(target = "publishedOn", source = "publishedOn", qualifiedByName = "mapDateToString")
    EventFullDto toEventFullDto(@Valid Event event);

    @Mapping(source = "initiator", target = "initiator")
    @Mapping(target = "eventDate", source = "eventDate", qualifiedByName = "mapDateToString")
    EventShortDto toEventShortDto(Event event);

    @Named("mapStringToDate")
    default LocalDateTime mapStringToDate(String timestamp) {
        return LocalDateTime.parse(timestamp, FORMATTER);
    }

    @Named("mapDateToString")
    default String mapDateToString(LocalDateTime timestamp) {
        if (timestamp != null) {
            return timestamp.format(FORMATTER);
        } else return null;
    }
}
