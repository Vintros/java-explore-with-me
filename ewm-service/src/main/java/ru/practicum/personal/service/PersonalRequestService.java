package ru.practicum.personal.service;

import ru.practicum.common.dto.ParticipationRequestDto;

import java.util.List;

public interface PersonalRequestService {
    ParticipationRequestDto createParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getAllParticipationRequests(Long userId);
}
