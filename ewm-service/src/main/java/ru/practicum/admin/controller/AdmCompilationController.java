package ru.practicum.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.service.AdmCompilationService;
import ru.practicum.common.dto.CompilationDto;
import ru.practicum.common.dto.NewCompilationDto;
import ru.practicum.common.dto.UpdateCompilationDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdmCompilationController {

    private final AdmCompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Create compilation {}", newCompilationDto);
        return service.createCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody UpdateCompilationDto updateCompilationDto) {
        log.info("Update compilation by id: {}, update: {}", compId, updateCompilationDto);
        return service.updateCompilation(compId, updateCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Delete compilation by id: {}", compId);
        service.deleteCompilation(compId);
    }
}
