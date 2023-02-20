package ru.practicum.personal.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.common.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PersonalEventService {
    EventFullDto createEvent(Long userId, EventDtoRequest eventDtoRequest);

    EventFullDto getEventById(Long userId, Long eventId, HttpServletRequest request);

    List<EventShortDto> getEventsByUserId(Long userId, Pageable pageable);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDtoRequest request);

    List<ParticipationRequestDto> getEventRequestsById(Long userId, Long eventId);

    EventRequestStatusUpdateResult considerationEventRequests(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request);
}
