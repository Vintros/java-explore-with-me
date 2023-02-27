package ru.practicum.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private String userName;
    private String text;
    private LocalDateTime created;
}
