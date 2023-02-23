package ru.practicum.personal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.admin.storage.AdmCategoryRepository;
import ru.practicum.admin.storage.AdmLocationRepository;
import ru.practicum.admin.storage.AdmUserRepository;
import ru.practicum.common.dto.*;
import ru.practicum.common.exceptions.EntityNoAccessException;
import ru.practicum.common.exceptions.EntityNotFoundException;
import ru.practicum.common.exceptions.IncorrectRequestException;
import ru.practicum.common.exceptions.StatsClientResponseException;
import ru.practicum.common.mapper.EventMapper;
import ru.practicum.common.mapper.RequestMapper;
import ru.practicum.common.model.*;
import ru.practicum.common.util.StateRequest;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.personal.storage.PersonalEventRepository;
import ru.practicum.personal.storage.PersonalRequestRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.common.util.State.*;

@Service
@RequiredArgsConstructor
public class PersonalEventServiceImpl implements PersonalEventService {

    private final PersonalEventRepository eventRepository;
    private final AdmUserRepository userRepository;
    private final AdmCategoryRepository categoryRepository;
    private final PersonalRequestRepository requestRepository;
    private final AdmLocationRepository locationRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;

    @Override
    public EventFullDto createEvent(Long userId, EventDtoRequest eventDtoRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user not found"));
        Category category = categoryRepository.findById(eventDtoRequest.getCategory()).orElseThrow(
                () -> new EntityNotFoundException("Field: category. Error: must not be blank. Value: null"));
        Event event = eventMapper.convertToEvent(user, category, eventDtoRequest);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.convertToEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("event not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EntityNoAccessException("you are not initiator");
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
        return eventMapper.convertToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Pageable pageable) {
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageable);
        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        List<ResponseHitDto> responsesHitDto = getHitsFromStatsServer(events);
        addHitsToEvents(events, responsesHitDto);
        return eventMapper.convertAllToEventShortDto(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDtoRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("event not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EntityNoAccessException("you are not initiator");
        }
        if (event.getState().equals(PUBLISHED)) {
            throw new EntityNoAccessException("event can't change");
        }
        switch (request.getStateAction()) {
            case CANCEL_REVIEW:
                event.setState(CANCELED);
                break;
            case SEND_TO_REVIEW:
                event.setState(PENDING);
        }
        updateEventFields(event, request);

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.convertToEventFullDto(updatedEvent);
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsById(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("event not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EntityNoAccessException("you are not initiator");
        }
        return requestMapper.convertAllToParticipationRequestDto(event.getRequests());
    }

    @Override
    public EventRequestStatusUpdateResult considerationEventRequests(Long userId, Long eventId,
                                                                     EventRequestStatusUpdateRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("event not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EntityNoAccessException("you are not initiator");
        }
        List<EventRequest> savedRequests = requestRepository.findAllByIdIn(request.getRequestIds());
        checkAccessRequests(savedRequests);

        List<EventRequest> confirmedRequests = new ArrayList<>();
        List<EventRequest> rejectedRequests = new ArrayList<>();
        switch (request.getStatus()) {
            case REJECTED:
                for (EventRequest savedRequest : savedRequests) {
                    savedRequest.setStatus(request.getStatus());
                    rejectedRequests.add(savedRequest);
                }
                break;
            case CONFIRMED:
                Long participationLimit = (long) event.getParticipantLimit();
                Long confirmedRequestsCount = event.getRequests().stream()
                        .filter((r) -> r.getStatus().equals(StateRequest.CONFIRMED))
                        .count();
                if (participationLimit.equals(confirmedRequestsCount) && participationLimit != 0L) {
                    throw new EntityNoAccessException("limit to participate in this event reached");
                }
                for (EventRequest savedRequest : savedRequests) {
                    boolean limitReached = checkParticipationLimitReached(participationLimit, confirmedRequestsCount);
                    if (limitReached) {
                        savedRequest.setStatus(StateRequest.REJECTED);
                        rejectedRequests.add(savedRequest);
                    } else {
                        savedRequest.setStatus(StateRequest.CONFIRMED);
                        confirmedRequests.add(savedRequest);
                        confirmedRequestsCount++;
                    }
                }
                break;
            case PENDING:
            case CANCELED:
                throw new IncorrectRequestException("Request must have status PENDING");
        }
        requestRepository.saveAll(savedRequests);
        return requestMapper.convertToStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private boolean checkParticipationLimitReached(Long participationLimit, Long confirmedRequests) {
        return participationLimit.equals(confirmedRequests) && participationLimit != 0L;
    }

    private void checkAccessRequests(List<EventRequest> savedRequests) {
        long pendingCount = savedRequests.stream()
                .filter((r) -> r.getStatus().equals(StateRequest.PENDING))
                .count();
        if (savedRequests.size() != pendingCount) {
            throw new EntityNoAccessException("request don't have status: pending");
        }
    }

    private void updateEventFields(Event event, UpdateEventDtoRequest request) {
        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory()).orElseThrow(
                    () -> new EntityNotFoundException("category not found"));
            event.setCategory(category);
        }
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
            locationRepository.save(request.getLocation());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
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

    private List<ResponseHitDto> getStats(ResponseEntity<Object> stats) {
        List<ResponseHitDto> responsesHitDto;
        TypeReference<List<ResponseHitDto>> typeRef = new TypeReference<>() {
        };
        try {
            responsesHitDto = objectMapper.readValue(
                    objectMapper.writeValueAsString(stats.getBody()), typeRef);
        } catch (JsonProcessingException e) {
            throw new StatsClientResponseException("Can't read response from stats client");
        }
        return responsesHitDto;
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
}
