package ru.practicum.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.Comment;

import java.time.LocalDateTime;

import static ru.practicum.util.Constant.FORMATTER;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "createdAt", source = "created", qualifiedByName = "mapDateToString")
    @Mapping(target = "authorId", source = "author.id")
    CommentDto toDto(Comment comment);

    @Named("mapDateToString")
    default String mapDateToString(LocalDateTime timestamp) {
        if (timestamp != null) {
            return timestamp.format(FORMATTER);
        } else return null;
    }
}
