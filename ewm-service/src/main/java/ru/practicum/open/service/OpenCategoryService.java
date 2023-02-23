package ru.practicum.open.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.common.dto.CategoryDto;

import java.util.List;

public interface OpenCategoryService {
    List<CategoryDto> getCategories(Pageable pageable);

    CategoryDto getCategoryById(Long id);
}
