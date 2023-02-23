package ru.practicum.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.storage.AdmCompilationRepository;
import ru.practicum.admin.storage.AdmEventRepository;
import ru.practicum.common.dto.CompilationDto;
import ru.practicum.common.dto.NewCompilationDto;
import ru.practicum.common.dto.UpdateCompilationDto;
import ru.practicum.common.exceptions.EntityNotFoundException;
import ru.practicum.common.mapper.CompilationMapper;
import ru.practicum.common.model.Compilation;
import ru.practicum.common.model.Event;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdmCompilationServiceImpl implements AdmCompilationService {

    private final AdmCompilationRepository admCompilationRepository;
    private final AdmEventRepository admEventRepository;
    private final CompilationMapper mapper;


    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = admEventRepository.findAllById(newCompilationDto.getEvents());
        if (events.size() != newCompilationDto.getEvents().size()) {
            throw new EntityNotFoundException("Events not founded");
        }
        Compilation compilation = mapper.convertToCompilation(newCompilationDto, events);
        Compilation savedCompilation = admCompilationRepository.save(compilation);
        return mapper.convertToCompilationDto(savedCompilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!admCompilationRepository.existsById(compId)) {
            throw new EntityNotFoundException("Compilation not founded");
        }
        admCompilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = admCompilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation not founded"));
        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }
        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }
        if (!updateCompilationDto.getEvents().isEmpty()) {
            List<Event> events = admEventRepository.findAllById(updateCompilationDto.getEvents());
            if (events.size() != updateCompilationDto.getEvents().size()) {
                throw new EntityNotFoundException("Events not founded");
            }
            compilation.setEvents(events);
        }
        Compilation updatedCompilation = admCompilationRepository.save(compilation);
        return mapper.convertToCompilationDto(updatedCompilation);
    }
}
