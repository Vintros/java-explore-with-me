package ru.practicum.open.service;

import ru.practicum.common.dto.EventFullDto;
import ru.practicum.common.dto.EventShortDto;
import ru.practicum.dto.EventRequestParams;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface OpenEventService {
    EventFullDto getEventById(Long id, HttpServletRequest request);

    List<EventShortDto> getEvents(EventRequestParams params, HttpServletRequest request);
}
