package ru.practicum.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.admin.storage.AdmCategoryRepository;
import ru.practicum.admin.storage.AdmEventRepository;
import ru.practicum.common.dto.CategoryDto;
import ru.practicum.common.exceptions.EntityNoAccessException;
import ru.practicum.common.exceptions.EntityNotFoundException;
import ru.practicum.common.mapper.CategoryMapper;
import ru.practicum.common.model.Category;

@Service
@RequiredArgsConstructor
public class AdmCategoryServiceImpl implements AdmCategoryService {

    private final AdmCategoryRepository categoryRepository;
    private final AdmEventRepository eventRepository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new EntityNoAccessException("Category name must be unique");
        }

        Category savedCategory = categoryRepository.save(mapper.convertToCategory(categoryDto));
        return mapper.convertToCategoryDto(savedCategory);
    }

    @Override
    public void deleteCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new EntityNotFoundException("Category not founded");
        }
        if (eventRepository.existsByCategory_Id(catId)) {
            throw new EntityNoAccessException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        if (!categoryRepository.existsById(catId)) {
            throw new EntityNotFoundException("Category not founded");
        }
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new EntityNoAccessException("Category name must be unique");
        }
        Category updatedCategory = categoryRepository.save(new Category(catId, categoryDto.getName()));
        return mapper.convertToCategoryDto(updatedCategory);
    }
}
