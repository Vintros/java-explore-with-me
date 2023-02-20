package ru.practicum.personal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.ParticipationRequestDto;
import ru.practicum.personal.service.PersonalRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class PersonalRequestController {

    private final PersonalRequestService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Create participation request to eventId {} by userId: {}", eventId, userId);
        return service.createParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Cancel participation request with id {} by userId: {}", requestId, userId);
        return service.cancelParticipationRequest(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllParticipationRequests(@PathVariable Long userId) {
        log.info("Get all participation requests by userId: {}", userId);
        return service.getAllParticipationRequests(userId);
    }


}
