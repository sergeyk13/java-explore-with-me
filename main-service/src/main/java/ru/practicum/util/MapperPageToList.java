package ru.practicum.util;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapperPageToList {
    public static <T, R> List<R> mapPageToList(Page<T> page, int from, int size, Function<T, R> mapper) {
        int elementOnPage = from % size;
        return page.stream()
                .skip(elementOnPage)
                .limit(size)
                .map(mapper)
                .collect(Collectors.toList());
    }
}
