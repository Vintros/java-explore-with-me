package ru.practicum.personal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.*;
import ru.practicum.common.exceptions.RequestNotValidException;
import ru.practicum.common.model.FromSizeRequest;
import ru.practicum.personal.service.PersonalEventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class PersonalEventController {

    private final PersonalEventService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody EventDtoRequest request) {
        if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new RequestNotValidException("Request not valid");
        }
        log.info("Create event: {} by userId: {}", request, userId);
        return service.createEvent(userId, request);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                    @Valid
                                    @RequestBody UpdateEventDtoRequest request) {
        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new RequestNotValidException("Request not valid");
        }
        log.info("Update event {} with id: {} by userId: {}", request, eventId, userId);
        return service.updateEvent(userId, eventId, request);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult considerationEventRequests(@PathVariable Long userId,
                                                                     @PathVariable Long eventId,
                                                                     @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Considerate event id {} requests by userId {} to: {}", eventId, userId, request);
        return service.considerationEventRequests(userId, eventId, request);
    }

    @GetMapping
    public List<EventShortDto> getEventsByUserId(@PathVariable Long userId, @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = FromSizeRequest.of(from, size, sort);
        log.info("Get events by userId: {}", userId);
        return service.getEventsByUserId(userId, pageable);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long userId, @PathVariable Long eventId, HttpServletRequest request) {
        log.info("Get event by id {} by userId: {}", eventId, userId);
        return service.getEventById(userId, eventId, request);
    }

    @GetMapping("{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequestsById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Get event requests by eventId {} by userId: {}", eventId, userId);
        return service.getEventRequestsById(userId, eventId);
    }
}

