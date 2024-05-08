package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.model.ValidationException;
import ru.practicum.mapper.StatisticMapper;
import ru.practicum.model.Statistic;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private StatsRepository statsRepository;

    @Transactional
    @Override
    public StatisticDto postHit(StatisticDto statisticDto) {
        Statistic statistic = StatisticMapper.INSTANCE.toStatistic(statisticDto);
        return StatisticMapper.INSTANCE.toStatisticDto(statsRepository.save(statistic));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        checkTime(start, end);
        Specification<Statistic> spec = Specification.where(null);
        spec = spec.and(StatsSpecification.timestampBetween(start, end));

        if (uris != null && !uris.isEmpty()) {
            spec = spec.and(StatsSpecification.byUris(uris));
        }

        if (unique) {
            assert uris != null;
            if (!uris.isEmpty()) {
                spec = spec.and(StatsSpecification.uniqueIps());
            }
        }

        List<Statistic> statistics = statsRepository.findAll(spec);
        Map<String, Integer> uriToHitsMap = new HashMap<>();

        for (String uri : uris) {
            List<String> uniqueIps = statsRepository.findUniqueIpsBetween(uri, start, end);
            int hits = unique ? uniqueIps.size() : statsRepository.countByUriAndTimestampBetween(uri, start, end);
            uriToHitsMap.put(uri, hits);
        }

        return uriToHitsMap.entrySet().stream()
                .map(entry -> {
                    String uri = entry.getKey();
                    int hits = entry.getValue();
                    Statistic statistic = statsRepository.findFirstByUri(uri);
                    if (statistic != null) {
                        return new ViewStats(statistic.getApp(), uri, hits);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(ViewStats::getHits).reversed())
                .collect(Collectors.toList());
    }

    private static void checkTime(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            log.error("Start time is after end time");
            throw new ValidationException("Не верно указан запрос дат");
        }
    }
}
