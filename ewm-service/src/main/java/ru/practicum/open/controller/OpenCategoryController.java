package ru.practicum.open.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.CategoryDto;
import ru.practicum.common.model.FromSizeRequest;
import ru.practicum.open.service.OpenCategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class OpenCategoryController {

    private final OpenCategoryService service;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = FromSizeRequest.of(from, size, sort);
        log.info("Get categories");
        return service.getCategories(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable Long id) {
        log.info("Get category by id {}", id);
        return service.getCategoryById(id);
    }

}
