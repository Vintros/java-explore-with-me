package ru.practicum.common.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.common.dto.CompilationDto;
import ru.practicum.common.dto.EventShortDto;
import ru.practicum.common.dto.NewCompilationDto;
import ru.practicum.common.model.Compilation;
import ru.practicum.common.model.Event;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CompilationMapper {

    private final EventMapper eventMapper;

    public Compilation convertToCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return new Compilation(
                null,
                newCompilationDto.getPinned(),
                newCompilationDto.getTitle(),
                events);
    }

    public CompilationDto convertToCompilationDto(Compilation compilation) {
        List<EventShortDto> eventsShortDto = eventMapper.convertAllToEventShortDto(compilation.getEvents());
        return new CompilationDto(
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle(),
                eventsShortDto
        );
    }

    public List<CompilationDto> convertAllToCompilationDto(List<Compilation> compilations) {
        return compilations.stream()
                .map(this::convertToCompilationDto)
                .collect(Collectors.toList());
    }

}
