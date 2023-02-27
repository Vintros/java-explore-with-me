package ru.practicum.admin.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.common.dto.EventFullDto;
import ru.practicum.common.dto.UpdateEventDtoRequest;
import ru.practicum.common.util.RequestParamsForEvents;

import java.util.List;

public interface AdmEventService {
    EventFullDto updateEvent(Long eventId, UpdateEventDtoRequest request);

    List<EventFullDto> getEvents(RequestParamsForEvents params, Pageable pageable);
}
