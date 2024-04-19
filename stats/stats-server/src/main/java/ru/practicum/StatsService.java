package ru.practicum;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    StatisticDto postHit(StatisticDto statistic);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
