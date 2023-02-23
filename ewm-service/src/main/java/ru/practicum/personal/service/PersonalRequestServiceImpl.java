package ru.practicum.personal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.admin.storage.AdmUserRepository;
import ru.practicum.common.dto.ParticipationRequestDto;
import ru.practicum.common.exceptions.EntityNoAccessException;
import ru.practicum.common.exceptions.EntityNotFoundException;
import ru.practicum.common.mapper.RequestMapper;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.EventRequest;
import ru.practicum.common.model.User;
import ru.practicum.common.util.State;
import ru.practicum.common.util.StateRequest;
import ru.practicum.personal.storage.PersonalEventRepository;
import ru.practicum.personal.storage.PersonalRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalRequestServiceImpl implements PersonalRequestService {

    private final PersonalRequestRepository requestRepository;
    private final AdmUserRepository userRepository;
    private final PersonalEventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("event not found"));
        List<EventRequest> requests = event.getRequests();

        if (event.getInitiator().getId().equals(userId)) {
            throw new EntityNoAccessException("you can't participate in your own event");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityNoAccessException("you can't participate in not published event");
        }
        List<EventRequest> confirmedRequests = requests.stream()
                .filter((r) -> r.getStatus().equals(StateRequest.CONFIRMED))
                .collect(Collectors.toList());
        if (event.getParticipantLimit().equals(confirmedRequests.size()) && event.getParticipantLimit() != 0) {
            throw new EntityNoAccessException("limit to participate in this event reached");
        }
        boolean isRequestExists = requests.stream().anyMatch((r) -> r.getRequester().getId().equals(userId));
        if (isRequestExists) {
            throw new EntityNoAccessException("you can't participate twice in event");
        }
        EventRequest eventRequest = EventRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(event.getRequestModeration() ? StateRequest.PENDING : StateRequest.CONFIRMED)
                .build();
        EventRequest savedRequest = requestRepository.save(eventRequest);
        return requestMapper.convertToParticipationRequestDto(savedRequest);
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        EventRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("request not found"));
        if (!request.getRequester().getId().equals(userId)) {
            throw new EntityNoAccessException("access denied");
        }
        if (request.getStatus().equals(StateRequest.CONFIRMED)) {
            throw new EntityNoAccessException("Request already confirmed");
        }
        request.setStatus(StateRequest.CANCELED);
        EventRequest updatedRequest = requestRepository.save(request);
        return requestMapper.convertToParticipationRequestDto(updatedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("user not found");
        }
        List<EventRequest> requests = requestRepository.findAllByRequester_Id(userId);
        return requestMapper.convertAllToParticipationRequestDto(requests);
    }


}
