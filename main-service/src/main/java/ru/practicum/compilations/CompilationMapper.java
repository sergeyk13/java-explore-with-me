package ru.practicum.compilations;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.model.Compilation;

@Mapper
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    @Mapping(target = "events", ignore = true)
    CompilationDto toDto(Compilation compilation);
}
