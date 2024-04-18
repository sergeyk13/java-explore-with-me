package ru.practicum;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.StatisticMapper;
import ru.practicum.model.Statistic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    private StatsRepository statsRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EndpointHit postHit(DtoStatistic statisticDto) {
        Statistic statistic = StatisticMapper.INSTANCE.toStatistic(statisticDto);
        return StatisticMapper.INSTANCE.toEndpointHit(statsRepository.save(statistic));
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);

        uris = Optional.ofNullable(uris).orElseGet(() -> {
            List<Statistic> statisticList = statsRepository.findByTimestampBetween(startTime, endTime);
            return new ArrayList<>(getUris(statisticList));
        });

        List<Statistic> allStatistics = statsRepository.findByTimestampBetween(startTime, endTime);

        return uris.stream()
                .flatMap(uri -> {
                    List<String> uniqueIps = new ArrayList<>(statsRepository.findUniqueIpsBetween(uri, startTime, endTime));
                    int hits = unique ? uniqueIps.size() : (int) allStatistics.stream()
                            .filter(statistic -> statistic.getUri().equals(uri))
                            .count();
                    if (hits > 0) {
                        return Stream.of(new ViewStats(allStatistics.stream()
                                .filter(statistic -> statistic.getUri().equals(uri))
                                .findFirst()
                                .map(Statistic::getApp)
                                .orElse(""), uri, hits));
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
