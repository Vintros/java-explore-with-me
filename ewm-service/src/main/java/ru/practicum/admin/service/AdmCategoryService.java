package ru.practicum.admin.service;

import ru.practicum.common.dto.CategoryDto;

public interface AdmCategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);
}
