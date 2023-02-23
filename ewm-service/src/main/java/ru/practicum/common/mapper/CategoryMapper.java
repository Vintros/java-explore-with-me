package ru.practicum.common.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.common.dto.CategoryDto;
import ru.practicum.common.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryMapper {

    public Category convertToCategory(CategoryDto categoryDto) {
        return new Category(null, categoryDto.getName());
    }

    public CategoryDto convertToCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public List<CategoryDto> convertAllToCategoryDto(List<Category> categories) {
        return categories.stream()
                .map(this::convertToCategoryDto)
                .collect(Collectors.toList());
    }
}
