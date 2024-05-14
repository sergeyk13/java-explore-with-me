package ru.practicum.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

import java.time.LocalDateTime;

import static ru.practicum.util.Constant.FORMATTER;

@Mapper
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(target = "created", source = "created", qualifiedByName = "mapStringToDate")
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "requester", ignore = true)
    Request toRequest(ParticipationRequestDto requestDto);

    @Mapping(target = "created", source = "created", qualifiedByName = "mapDateToString")
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto toParticipationRequestDto(Request request);

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
