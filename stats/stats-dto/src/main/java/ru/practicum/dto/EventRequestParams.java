package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import util.SortParam;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class EventRequestParams {

    private String text;
    private final List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private SortParam sort;
    private Integer from;
    private Integer size;

}
