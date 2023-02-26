package ru.practicum.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RequestParamsForComments {

    private Long userId;
    private StateComment state;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
}
