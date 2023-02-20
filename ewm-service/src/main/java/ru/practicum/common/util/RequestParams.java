package ru.practicum.common.util;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestParams {

    private final List<Long> users;
    private final List<State> states;
    private final List<Long> categories;
    private final LocalDateTime rangeStart;
    private final LocalDateTime rangeEnd;
}
