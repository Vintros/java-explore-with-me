package ru.practicum.common.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.common.dto.ParticipationRequestDto;
import ru.practicum.common.dto.EventRequestStatusUpdateResult;
import ru.practicum.common.model.EventRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestMapper {

    public ParticipationRequestDto convertToParticipationRequestDto(EventRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public List<ParticipationRequestDto> convertAllToParticipationRequestDto(List<EventRequest> requests) {
        return requests.stream()
                .map(this::convertToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult convertToStatusUpdateResult(List<EventRequest> confirmedRequests,
                                                                      List<EventRequest> rejectedRequests) {
        return new EventRequestStatusUpdateResult(
                convertAllToParticipationRequestDto(confirmedRequests),
                convertAllToParticipationRequestDto(rejectedRequests));
    }
}
