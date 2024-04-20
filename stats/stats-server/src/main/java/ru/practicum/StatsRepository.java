package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Statistic;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Statistic, Long> {
    List<Statistic> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<Statistic> getStatisticsByUriAndTimestampBetween(String uri, LocalDateTime start, LocalDateTime end);

    @Query("SELECT DISTINCT s.ip FROM Statistic s WHERE s.uri LIKE :uri  AND s.timestamp BETWEEN :start AND :end")
    List<String> findUniqueIpsBetween(@Param("uri") String uri, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    int countByUriAndTimestampBetween(String uri, LocalDateTime start, LocalDateTime end);

    Statistic findFirstByUri(String uri);
}
