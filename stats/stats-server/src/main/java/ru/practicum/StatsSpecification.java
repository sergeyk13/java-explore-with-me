package ru.practicum;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.model.Statistic;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Collection;

public class StatsSpecification {
    public static Specification<Statistic> byUris(Collection<String> uris) {
        return (Root<Statistic> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                root.get("uri").in(uris);
    }

    public static Specification<Statistic> uniqueIps() {
        return (Root<Statistic> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            query.distinct(true);
            query.select(root.get("ip"));
            return query.getRestriction();
        };
    }

    public static Specification<Statistic> timestampBetween(LocalDateTime start, LocalDateTime end) {
        return (Root<Statistic> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Predicate betweenPredicate = criteriaBuilder.between(root.get("timestamp"), start, end);
            return betweenPredicate;
        };
    }
}
