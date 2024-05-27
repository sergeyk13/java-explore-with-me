package ru.practicum.comment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@AllArgsConstructor
@Validated
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{userId}/events/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable long userId, @PathVariable long eventId,
                                 @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Controller Add comment: {}", newCommentDto);
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{userId}/events/{eventId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable long userId, @PathVariable long eventId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.info("Controller Update comment: {}", commentDto);
        return commentService.updateComment(userId, eventId, commentDto);
    }

    @GetMapping("{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getComment(@PathVariable long userId, @PathVariable long commentId) {
        log.info("Controller Get comment: {}", commentId);
        return commentService.getComment(userId, commentId);
    }

    @GetMapping("/{userId}/events/{eventId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getComments(@PathVariable long userId,
                                        @PathVariable long eventId,
                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        log.info("Controller Get all comments for user: {}", userId);
        return commentService.getComments(userId, eventId, from, size);
    }

    @DeleteMapping("{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long userId, @PathVariable long commentId) {
        log.info("Controller Delete comment: {}", commentId);
        commentService.deleteComment(userId, commentId);
    }
}