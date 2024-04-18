package ru.practicum;

import java.util.List;

public interface StatsService {
    EndpointHit postHit(DtoStatistic statistic);

    List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique);
}
