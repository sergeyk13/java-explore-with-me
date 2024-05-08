package ru.practicum;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.model.Statistic;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StatsSpecification {
    public static Specification<Statistic> byUris(Collection<String> uris) {
        return (Root<Statistic> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String uri : uris) {
                predicates.add(criteriaBuilder.like(root.get("uri"), "%" + uri + "%"));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
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
