package ru.practicum.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor
public class NewCompilationDto {

    private Boolean pinned;

    @NotBlank
    private String title;

    private List<Long> events;


}
