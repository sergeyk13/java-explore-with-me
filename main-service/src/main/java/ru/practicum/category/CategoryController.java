package ru.practicum.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryShortDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
@Slf4j
@Validated
public class CategoryController {
    CategoryService categoryService;

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

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable long catId) {
        log.info("Get category: {}", catId);
        return categoryService.getCategory(catId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories(
            @RequestParam(required = false, name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, name = "size", defaultValue = "10") @PositiveOrZero int size) {
        log.info("Get all categories");
        return categoryService.getCategories(from, size);
    }

}
