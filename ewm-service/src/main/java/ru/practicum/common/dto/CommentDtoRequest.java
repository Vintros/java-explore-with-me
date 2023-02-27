package ru.practicum.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class CommentDtoRequest {

    @NotBlank
    @Size(max = 1000)
    private String text;

}
