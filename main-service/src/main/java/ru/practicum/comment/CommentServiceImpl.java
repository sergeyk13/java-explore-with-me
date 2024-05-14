package ru.practicum.comment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.error.model.ConflictException;
import ru.practicum.error.model.NotFoundException;
import ru.practicum.error.model.ValidationException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;
import ru.practicum.util.MapperPageToList;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto addComment(long userId, long eventId, NewCommentDto newCommentDto) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        Comment comment = new Comment();
        comment.setAuthor(user);
        if (!(event.getState().equals(State.PUBLISHED))) {
            log.error("Only published events can has comment");
            throw new ConflictException("Only published events can has comment");
        }
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        comment.setText(newCommentDto.getText());
        log.info("Comment created text size: {} author:{}, for event:{}", comment.getText().length(), userId, eventId);
        return CommentMapper.INSTANCE.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto updateComment(long userId, long eventId, CommentDto commentDto) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        Comment comment = checkComment(commentDto.getId());
        checkOwner(user.getId(), commentDto.getAuthorId());
        if (commentDto.getText() != null) {
            comment.setText(commentDto.getText());
            log.info("Update comment text: {}", commentDto.getText());
        } else {
            log.error("Comment text is empty");
            throw new ValidationException("Comment text is empty");
        }
        return CommentMapper.INSTANCE.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getComment(long userId, long commentId) {
        Comment comment = checkComment(commentId);
        checkOwner(userId, comment.getAuthor().getId());
        return CommentMapper.INSTANCE.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(long userId, long eventId, int from, int size) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from, size, sortById);
        Page<Comment> comments = commentRepository.findAllByEventAndAuthor(event, user, page);
        log.info("Comments count: {}", comments.getTotalElements());
        return MapperPageToList.mapPageToList(comments, from, size, CommentMapper.INSTANCE::toDto);
    }

    @Override
    public void deleteComment(long userId, long commentId) {
        User user = checkUser(userId);
        Comment comment = checkComment(commentId);
        checkOwner(userId, comment.getAuthor().getId());
        commentRepository.delete(comment);
        log.info("Comment deleted: {}", commentId);
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with id: {} not found", userId);
            return new NotFoundException("User with id " + userId + " not found");
        });
    }

    private Event checkEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with id: {} not found", eventId);
            return new NotFoundException("Event with id " + eventId + " not found");
        });
    }

    private Comment checkComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("Comment with id: {} not found", commentId);
            return new NotFoundException("Comment with id " + commentId + " not found");
        });
    }

    private void checkOwner(long userId, long authorId) {
        if (authorId != userId) {
            log.error("User with id: {} is not owner of comment", userId);
            throw new ConflictException("User with id " + userId + " is not owner of comment ");
        }
    }
}
