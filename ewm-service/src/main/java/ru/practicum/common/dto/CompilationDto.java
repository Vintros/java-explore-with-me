package ru.practicum.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CompilationDto {

    private Long id;

    private Boolean pinned;

    private String title;

    private List<EventShortDto> events;

}
