package ru.practicum.ewmservice.mapper;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.input.NewCategoryDto;
import ru.practicum.ewmservice.model.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName()).build();
    }

    public static Category toModel(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName()).build();
    }

    public static Category toModel(NewCategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName()).build();
    }
}
