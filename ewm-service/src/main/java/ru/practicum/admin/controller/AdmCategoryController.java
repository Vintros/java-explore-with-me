package ru.practicum.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.service.AdmCategoryService;
import ru.practicum.common.dto.CategoryDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Slf4j
public class AdmCategoryController {

    private final AdmCategoryService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Create category {}", categoryDto);
        return service.createCategory(categoryDto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto, @PathVariable Long catId) {
        log.info("Update category by id: {}, update: {}", catId, categoryDto);
        return service.updateCategory(catId, categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Delete category by id: {}", catId);
        service.deleteCategory(catId);
    }
}
