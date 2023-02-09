package ru.practicum.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.model.RequestHit;
import ru.practicum.model.ResponseHit;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Mapper {

    public RequestHit convertToRequestHit(RequestHitDto dto) {
        return new RequestHit(null, dto.getApp(), dto.getUri(), dto.getIp(), dto.getTimestamp());
    }

    public ResponseHitDto convertToResponseHitDto(ResponseHit responseHit) {
        return new ResponseHitDto(responseHit.getApp(), responseHit.getUri(), responseHit.getHits());
    }

    public List<ResponseHitDto> convertAllToResponseHitDto(List<ResponseHit> hits) {
        return hits.stream()
                .map(this::convertToResponseHitDto)
                .collect(Collectors.toList());
    }

}
