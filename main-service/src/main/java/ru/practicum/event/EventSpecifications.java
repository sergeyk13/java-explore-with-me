package ru.practicum.event;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.model.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Collection;

public class EventSpecifications {

    public static Specification<Event> byInitiatorIn(Collection<User> initiators) {
        return (Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                root.get("initiator").in(initiators);
    }

    public static Specification<Event> byStateIn(Collection<State> states) {
        return (Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                root.get("state").in(states);
    }

    public static Specification<Event> byCategoryIdIn(Collection<Category> category) {
        return (Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                root.get("category").in(category);
    }

    public static Specification<Event> byEventDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.between(root.get("eventDate"), startDate, endDate);
    }

    public static Specification<Event> byAnnotation(String searchText) {
        return  (Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + searchText.toLowerCase() + "%");
    }

    public static Specification<Event> byPaid(boolean paid) {
        return (Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("paid"), paid);
    }

    public static Specification<Event> byAvailable(boolean onlyAvailable) {
        return (Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("available"), onlyAvailable);
    }
}

