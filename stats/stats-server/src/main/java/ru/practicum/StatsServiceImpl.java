package ru.practicum;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.StatisticMapper;
import ru.practicum.model.Statistic;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
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
        uris = Optional.ofNullable(uris).orElseGet(() -> {
            List<Statistic> statisticList = statsRepository.findByTimestampBetween(start, end);
            return new ArrayList<>(getUris(statisticList));
        });

        List<Statistic> allStatistics = statsRepository.findByTimestampBetween(start, end);

        return uris.stream()
                .flatMap(uri -> {
                    List<String> uniqueIps = new ArrayList<>(statsRepository.findUniqueIpsBetween(uri, start, end));
                    int hits = unique ? uniqueIps.size() : statsRepository.countByUriAndTimestampBetween(uri, start, end);
                    if (hits > 0) {
                        return Stream.of(new ViewStats(statsRepository.findFirstByUri(uri).getApp(), uri, hits));
                    } else {
                        return Stream.empty();
                    }
                })
                .sorted(Comparator.comparingLong(ViewStats::getHits).reversed())
                .collect(Collectors.toList());
    }

    private Set<String> getUris(List<Statistic> statisticList) {
        return statisticList.stream()
                .map(Statistic::getUri)
                .collect(Collectors.toSet());
    }
}
