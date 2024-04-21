package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryShortDto;
import ru.practicum.category.model.Category;
import ru.practicum.util.MapperPageToList;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto postCategory(CategoryShortDto shortDto) {
        log.info("Save category: {}", shortDto.getName());
        return CategoryMapper.INSTANCE.toDto(categoryRepository.save(new Category(shortDto.getName())));
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        log.info("Delete category: {}", catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryShortDto shortDto, long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new RuntimeException(String.format("Request with ID:%d not found",catId)));
        category.setName(shortDto.getName());

        log.info("Updating category: {}", category);
        return CategoryMapper.INSTANCE.toDto(category);
    }


    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(long catId) {
        log.info("Getting category by id: {}", catId);
        return CategoryMapper.INSTANCE.toDto(categoryRepository.findById(catId).orElseThrow(() ->
                new RuntimeException(String.format("Request with ID:%d not found",catId))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoties(int from, int size) {
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from, size, sortById);
        Page<Category> categoryPage = categoryRepository.findAll(page);
        return MapperPageToList.mapPageToList(categoryPage,from,size, CategoryMapper.INSTANCE::toDto);
    }
}