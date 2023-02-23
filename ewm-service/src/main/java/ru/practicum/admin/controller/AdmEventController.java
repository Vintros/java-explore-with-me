package ru.practicum.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.service.AdmEventService;
import ru.practicum.common.dto.EventFullDto;
import ru.practicum.common.dto.UpdateEventDtoRequest;
import ru.practicum.common.exceptions.RequestNotValidException;
import ru.practicum.common.model.FromSizeRequest;
import ru.practicum.common.util.RequestParams;
import ru.practicum.common.util.State;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Slf4j
public class AdmEventController {

    private final AdmEventService service;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventDtoRequest request) {
        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new RequestNotValidException("Request not valid");
        }
        log.info("Update event by id: {}, update: {}", eventId, request);
        return service.updateEvent(eventId, request);
    }

    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = FromSizeRequest.of(from, size, sort);
        RequestParams params = new RequestParams(users, states, categories, rangeStart, rangeEnd);
        log.info("Get events by params: {}", params);
        return service.getEvents(params, pageable);
    }

}
