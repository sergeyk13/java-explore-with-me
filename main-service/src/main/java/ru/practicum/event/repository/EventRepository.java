package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
            @NotNull State state, @NotBlank String annotation, Collection<Long> category_id, @NotNull boolean paid, @NotNull LocalDateTime eventDate, @NotNull LocalDateTime eventDate2, @NotNull boolean available, Pageable pageable);

    Page<Event> findAllByStateAndAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidAndEventDateAfterAndAvailable(
            @NotNull State state, @NotBlank String annotation, Collection<Long> category_id, @NotNull boolean paid, @NotNull LocalDateTime eventDate, @NotNull boolean available, Pageable pageable);

    Page<Event> findAllByPaidAndEventDateAfter(boolean paid, LocalDateTime endDate, Pageable pageable);

    Page<Event> findAllByPaidAndEventDateBetween(boolean paid, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> ids);

    List<Long> findIdsByCompilation(Long compilationId);

    List<Event> findAllByCompilation(Long compilationId);

   Optional<Event> findByIdAndState(Long id, State state);
}
