package ru.practicum.category;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryShortDto;

import java.util.List;

public interface CategoryService {
    CategoryDto postCategory(CategoryShortDto shortDto);

    void deleteCategory(long catId);

    CategoryDto updateCategory(CategoryShortDto shortDto, long catId);

    CategoryDto getCategory(long catId);

    List<CategoryDto> getCategories(int from, int size);
}
