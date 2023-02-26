package ru.practicum.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.common.model.User;
import ru.practicum.common.util.StateComment;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentFullDto {
    private Long id;
    private User user;
    private Long eventId;
    private String text;
    private LocalDateTime created;
    private StateComment state;

}
