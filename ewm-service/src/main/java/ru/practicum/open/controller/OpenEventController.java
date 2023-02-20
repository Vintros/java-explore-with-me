package ru.practicum.open.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.EventFullDto;
import ru.practicum.common.dto.EventShortDto;
import ru.practicum.dto.EventRequestParams;
import ru.practicum.open.service.OpenEventService;
import util.SortParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class OpenEventController {

    private final OpenEventService service;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) SortParam sort,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        EventRequestParams params = new EventRequestParams(text, categories, paid, start, end, onlyAvailable, sort, from, size);
        log.info("Get events by params: {}", params);
        return service.getEvents(params, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Get events by id {}", id);
        return service.getEventById(id, request);
    }


}
