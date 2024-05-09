package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.CategoryService;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryShortDto;

import javax.validation.Valid;

@RestController
@RequestMapping
@AllArgsConstructor
@Slf4j
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@RequestBody @Valid CategoryShortDto shortDto) {
        log.info("Post category: {}", shortDto);
        return categoryService.postCategory(shortDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long catId) {
        log.info("Delete category: {}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto updateCategory(@RequestBody @Valid CategoryShortDto shortDto, @PathVariable long catId) {
        log.info("Patch category id: {}, name {}", catId, shortDto);
        return categoryService.updateCategory(shortDto, catId);
    }
}
