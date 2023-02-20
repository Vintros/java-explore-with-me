package ru.practicum.open.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.common.dto.CategoryDto;
import ru.practicum.common.exceptions.EntityNotFoundException;
import ru.practicum.common.mapper.CategoryMapper;
import ru.practicum.common.model.Category;
import ru.practicum.open.storage.OpenCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenCategoryServiceImpl implements OpenCategoryService {

    private final OpenCategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        List<Category> categories = repository.findAll(pageable).toList();
        return mapper.convertAllToCategoryDto(categories);
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not founded"));
        return mapper.convertToCategoryDto(category);
    }
}
