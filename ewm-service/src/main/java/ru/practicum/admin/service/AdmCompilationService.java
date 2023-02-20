package ru.practicum.admin.service;

import ru.practicum.common.dto.CompilationDto;
import ru.practicum.common.dto.NewCompilationDto;
import ru.practicum.common.dto.UpdateCompilationDto;

public interface AdmCompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto);
}
