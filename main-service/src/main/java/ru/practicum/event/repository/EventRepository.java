package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByInitiator(User user, Pageable pageable);

    Page<Event> findAllByInitiatorInAndStateInAndCategoryIdInAndEventDateBetween(List<Long> users,
                                                                                 List<String> states,
                                                                                 List<Long> categories,
                                                                                 LocalDateTime startDate,
                                                                                 LocalDateTime endDate,
                                                                                 Pageable pageable);

    Page<Event> findAllByStateAndAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidAndEventDateBetweenAndAvailable(
            String state,
            String annotationText,
            List<Long> categoryIds,
            boolean paid,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean available,
            Pageable pageabla);

    Page<Event> findAllByStateAndAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidAndEventDateAfterAndAvailable(
            String state,
            String annotationText,
            List<Long> categoryIds,
            boolean paid,
            LocalDateTime startDate,
            boolean available,
            Pageable pageabla);
}
