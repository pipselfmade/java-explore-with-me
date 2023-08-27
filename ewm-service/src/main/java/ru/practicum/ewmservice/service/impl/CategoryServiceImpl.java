package ru.practicum.ewmservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.input.NewCategoryDto;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.exception.ValidatedException;
import ru.practicum.ewmservice.mapper.CategoryMapper;
import ru.practicum.ewmservice.model.Category;
import ru.practicum.ewmservice.repository.CategoryRepository;
import ru.practicum.ewmservice.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(NewCategoryDto categoryDto) {
        log.info("Task created new category {}", categoryDto);
        isNameCategoryExist(categoryDto.getName());
        final Category category = CategoryMapper.toModel(categoryDto);
        log.debug("try save new category {}", category);
        final Category savedCategory = categoryRepository.save(category);
        log.debug("Category save. Save category: '{}'", savedCategory);
        return CategoryMapper.toDto(savedCategory);
    }

    @Override
    public void delete(Long catId) {
        log.info("Task delete category by id={}", catId);
        isCategoryExistById(catId);
        categoryRepository.deleteById(catId);
        log.debug("Category with id {} deleted - {}", catId, !categoryRepository.existsById(catId));
    }

    @Override
    public CategoryDto update(Long catId, NewCategoryDto categoryDto) {
        log.info("Task update category. Category id='{}', update='{}'", catId, categoryDto);
        final Category oldCategory = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id='" + catId + "' not found"));
        if (categoryDto.getName() != null) {
            oldCategory.setName(categoryDto.getName());
            log.debug("Name assigned. name = '{}'", oldCategory.getName());
        }
        final Category updatedCategory = categoryRepository.save(oldCategory);
        log.debug("Category updated successfully");
        return CategoryMapper.toDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        log.info("Get all category, config pageable = {}", pageable);
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long catId) {
        log.info("Task get category by id={}", catId);
        return CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id='" + catId + "' not found")));
    }

    private void isCategoryExistById(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with id='" + catId + "' not found");
        }
    }

    private void isNameCategoryExist(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ValidatedException("Category name '" + name + "' is not unique");
        }
    }
}
