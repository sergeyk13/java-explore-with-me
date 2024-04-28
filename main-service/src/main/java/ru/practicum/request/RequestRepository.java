package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByEventAndRequester(Event event, User requester);

    Long countByEvent(Event event);

    List<Request> findAllByRequester(User user);
}
