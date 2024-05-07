package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.category.model.Category;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByInitiator(User user, Pageable pageable);

    Page<Event> findAll(Specification<Event> spec, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> ids);

    List<Long> findIdsByCompilation(Compilation compilation);

    List<Event> findAllByCompilation(Compilation compilation);

    Optional<Event> findByIdAndState(Long id, State state);

    List<Event> findAllByCategory(Category category);
}
