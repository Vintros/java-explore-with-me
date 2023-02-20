package ru.practicum.personal.controller;

import lombok.RequiredArgsConstructor;
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
public class PersonalEventController {

    private final PersonalEventService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody EventDtoRequest request) {
        if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new RequestNotValidException("Request not valid");
        }
        return service.createEvent(userId, request);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                    @Valid
                                    @RequestBody UpdateEventDtoRequest request) {
        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new RequestNotValidException("Request not valid");
        }
        return service.updateEvent(userId, eventId, request);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult considerationEventRequests(@PathVariable Long userId,
                                                                     @PathVariable Long eventId,
                                                                     @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        return service.considerationEventRequests(userId, eventId, request);
    }

    @GetMapping
    public List<EventShortDto> getEventsByUserId(@PathVariable Long userId, @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = FromSizeRequest.of(from, size, sort);
        return service.getEventsByUserId(userId, pageable);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long userId, @PathVariable Long eventId, HttpServletRequest request) {
        return service.getEventById(userId, eventId, request);
    }

    @GetMapping("{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequestsById(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getEventRequestsById(userId, eventId);
    }
}

