package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.CategoryService;
import ru.practicum.category.dto.CategoryDto;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
@Slf4j
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable long catId) {
        log.info("Get category: {}", catId);
        return categoryService.getCategory(catId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories(@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        log.info("Get all categories");
        return categoryService.getCategories(from, size);
    }
}
