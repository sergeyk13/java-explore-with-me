package ru.practicum.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByEventAndAuthor(Event event, User author, Pageable pageable);
}
