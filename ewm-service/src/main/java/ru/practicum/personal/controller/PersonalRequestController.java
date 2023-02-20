package ru.practicum.personal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.ParticipationRequestDto;
import ru.practicum.personal.service.PersonalRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PersonalRequestController {

    private final PersonalRequestService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return service.createParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return service.cancelParticipationRequest(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllParticipationRequests(@PathVariable Long userId) {
        return service.getAllParticipationRequests(userId);
    }


}
