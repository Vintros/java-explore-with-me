package ru.practicum.open.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.common.dto.EventFullDto;
import ru.practicum.common.dto.EventShortDto;
import ru.practicum.common.exceptions.EntityNotFoundException;
import ru.practicum.common.exceptions.StatsClientResponseException;
import ru.practicum.common.mapper.EventMapper;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.QEvent;
import ru.practicum.common.util.StateRequest;
import ru.practicum.dto.EventRequestParams;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.open.storage.OpenEventRepository;
import util.SortParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.common.util.State.PUBLISHED;

@Service
@RequiredArgsConstructor
public class OpenEventServiceImpl implements OpenEventService {

    private final OpenEventRepository eventRepository;
    private final EventMapper mapper;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;

    @Override
    public EventFullDto getEventById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (!event.getState().equals(PUBLISHED)) {
            throw new EntityNotFoundException("Event not found");
        }
        ResponseEntity<Object> stats = statsClient.getStats(
                event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                List.of(request.getRequestURI()),
                false);
        List<ResponseHitDto> responsesHitDto = getStats(stats);
        if (responsesHitDto.isEmpty()) {
            event.setViews(0L);
        } else {
            event.setViews(responsesHitDto.get(0).getHits());
        }
        saveHit(request);
        return mapper.convertToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEvents(EventRequestParams params, HttpServletRequest request) {
        List<Event> events = getEventsFromRepository(params);
        List<ResponseHitDto> responsesHitDto = getHitsFromStatsServer(events);
        addHitsToEvents(events, responsesHitDto);
        sortEvents(events, params);
        List<Event> eventsPage = paginateEvents(events, params);
        saveHit(request);
        return mapper.convertAllToEventShortDto(eventsPage);
    }

    private List<ResponseHitDto> getStats(ResponseEntity<Object> stats) {
        List<ResponseHitDto> responsesHitDto;
        TypeReference<List<ResponseHitDto>> typeRef = new TypeReference<>() {
        };
        try {
            responsesHitDto = objectMapper.readValue(
                    objectMapper.writeValueAsString(stats.getBody()), typeRef);
        } catch (JsonProcessingException e) {
            throw new StatsClientResponseException("Can't read response from stats client" + e.getMessage());
        }
        return responsesHitDto;
    }

    private List<Event> paginateEvents(List<Event> events, EventRequestParams params) {
        int end = Math.min((params.getFrom() + params.getSize()), events.size());
        return events.subList(params.getFrom(), end);
    }

    private List<Event> getEventsFromRepository(EventRequestParams params) {
        List<Event> events = new ArrayList<>();
        QEvent qEvent = QEvent.event;
        BooleanExpression predicate = qEvent.state.eq(PUBLISHED);

        if (params.getText() != null) {
            predicate = predicate.and(qEvent.annotation.containsIgnoreCase(params.getText())
                    .or(qEvent.description.containsIgnoreCase(params.getText())));
        }
        if (params.getCategories() != null) {
            predicate = predicate.and(qEvent.category.id.in(params.getCategories()));
        }
        if (params.getPaid() != null) {
            predicate = predicate.and(qEvent.paid.eq(params.getPaid()));
        }
        if (params.getRangeStart() != null) {
            predicate = predicate.and(qEvent.eventDate.after(params.getRangeStart()));
        }
        if (params.getRangeEnd() != null) {
            predicate = predicate.and(qEvent.eventDate.before(params.getRangeEnd()));
        }
        if (params.getRangeStart() == null && params.getRangeEnd() == null) {
            predicate = predicate.and(qEvent.eventDate.after(LocalDateTime.now()));
        }
        eventRepository.findAll(predicate).forEach(events::add);
        if (params.getOnlyAvailable()) {
            events = events.stream()
                    .filter((e) -> e.getParticipantLimit() != e.getRequests().stream()
                            .filter((r) -> r.getStatus().equals(StateRequest.CONFIRMED)).count()
                            || e.getParticipantLimit() == 0L)
                    .collect(Collectors.toList());
        }
        return events;
    }

    private void sortEvents(List<Event> events, EventRequestParams params) {
        if (params.getSort() == null || params.getSort().equals(SortParam.VIEWS)) {
            events.sort(Comparator.comparing(Event::getViews));
            return;
        }
        if (params.getSort().equals(SortParam.EVENT_DATE)) {
            Comparator<Event> comparator = (e1, e2) -> {
                if (e1.getEventDate().isAfter(e2.getEventDate())) {
                    return 1;
                } else if (e1.getEventDate().equals(e2.getEventDate())) {
                    return -0;
                } else {
                    return -1;
                }
            };
            events.sort(comparator);
        }
    }

    private void addHitsToEvents(List<Event> events, List<ResponseHitDto> responsesHitDto) {
        Map<Long, Long> eventIdAndHits = new HashMap<>();
        for (ResponseHitDto responseHitDto : responsesHitDto) {
            String uri = responseHitDto.getUri();
            Long id = Long.getLong(uri.substring(uri.lastIndexOf("/" + 1)));
            eventIdAndHits.put(id, responseHitDto.getHits());
        }
        for (Event event : events) {
            if (eventIdAndHits.get(event.getId()) != null) {
                event.setViews(eventIdAndHits.get(event.getId()));
            } else {
                event.setViews(0L);
            }
        }
    }

    private List<ResponseHitDto> getHitsFromStatsServer(List<Event> events) {
        List<String> uris = events.stream()
                .map((e) -> "/events/" + e.getId())
                .collect(Collectors.toList());
        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .sorted()
                .findFirst()
                .orElseThrow(() -> new  EntityNotFoundException("event don't have creation time"));
        ResponseEntity<Object> stats = statsClient.getStats(
                start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                uris,
                false);
        return getStats(stats);
    }

    private void saveHit(HttpServletRequest request) {
        RequestHitDto requestHitDto = new RequestHitDto(
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
        statsClient.saveHit(requestHitDto);
    }
}
