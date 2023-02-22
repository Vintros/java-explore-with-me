package ru.practicum.common.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.common.dto.EventDtoRequest;
import ru.practicum.common.dto.EventFullDto;
import ru.practicum.common.dto.EventShortDto;
import ru.practicum.common.model.Category;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.User;
import ru.practicum.common.util.State;
import ru.practicum.common.util.StateRequest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventMapper {

    public Event convertToEvent(User user, Category category, EventDtoRequest eventDtoRequest) {
        return Event.builder()
                .id(null)
                .annotation(eventDtoRequest.getAnnotation())
                .category(category)
                .requests(new ArrayList<>())
                .createdOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .description(eventDtoRequest.getDescription())
                .eventDate(eventDtoRequest.getEventDate())
                .initiator(user)
                .location(eventDtoRequest.getLocation())
                .paid(eventDtoRequest.getPaid())
                .participantLimit(eventDtoRequest.getParticipantLimit())
                .publishedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .requestModeration(eventDtoRequest.getRequestModeration())
                .state(State.PENDING)
                .title(eventDtoRequest.getTitle())
                .build();
    }

    public EventFullDto convertToEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .confirmedRequests(event.getRequests().stream()
                        .map((r) -> r.getStatus().equals(StateRequest.CONFIRMED))
                        .count())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public EventShortDto convertToEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .confirmedRequests(event.getRequests().stream()
                        .map((r) -> r.getStatus().equals(StateRequest.CONFIRMED))
                        .count())
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public List<EventShortDto> convertAllToEventShortDto(List<Event> events) {
        return events.stream()
                .map(this::convertToEventShortDto)
                .collect(Collectors.toList());
    }

    public List<EventFullDto> convertAllToEventFullDto(List<Event> events) {
        return events.stream()
                .map(this::convertToEventFullDto)
                .collect(Collectors.toList());
    }

}
