package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE r.event.id = :eventId AND r.requester.id = :userId")
    Optional<Request> findByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findAllByEvent(Event event);


    Long countByEvent(Event event);

    List<Request> findAllByRequester(User user);
}
