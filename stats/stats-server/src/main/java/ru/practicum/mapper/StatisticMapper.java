package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.StatisticDto;
import ru.practicum.ViewStats;
import ru.practicum.model.Statistic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper
public interface StatisticMapper {
    StatisticMapper INSTANCE = Mappers.getMapper(StatisticMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "mapTimestamp")
    Statistic toStatistic(StatisticDto statisticDto);

    StatisticDto toStatisticDto(Statistic statistic);

    @Mapping(target = "hits", source = "hits")
    ViewStats toViewStats(Statistic statistic, int hits);

    @Named("mapTimestamp")
    default LocalDateTime mapTimestamp(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(timestamp, formatter);
    }
}
