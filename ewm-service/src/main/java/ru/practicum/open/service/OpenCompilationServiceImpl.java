package ru.practicum.open.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.common.dto.CompilationDto;
import ru.practicum.common.exceptions.EntityNotFoundException;
import ru.practicum.common.mapper.CompilationMapper;
import ru.practicum.common.model.Compilation;
import ru.practicum.common.model.QCompilation;
import ru.practicum.open.storage.OpenCompilationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenCompilationServiceImpl implements OpenCompilationService {

    private final OpenCompilationRepository compilationRepository;
    private final CompilationMapper mapper;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
        QCompilation qCompilation = QCompilation.compilation;
        BooleanExpression predicate = qCompilation.isNotNull();

        if (pinned != null) {
            predicate = predicate.and(qCompilation.pinned.eq(pinned));
        }
        List<Compilation> compilations = compilationRepository.findAll(predicate, pageable).toList();
        return mapper.convertAllToCompilationDto(compilations);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation not founded"));
        return mapper.convertToCompilationDto(compilation);

    }
}
