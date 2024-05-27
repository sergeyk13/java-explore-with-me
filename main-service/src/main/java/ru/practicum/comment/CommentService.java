package ru.practicum.comment;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(long userId, long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(long userId, long eventId, CommentDto commentDto);

    CommentDto getComment(long userId, long id);

    List<CommentDto> getComments(long userId, long eventId, int from, int size);

    void deleteComment(long userId, long commentId);
}
