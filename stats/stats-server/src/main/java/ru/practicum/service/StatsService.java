package ru.practicum.service;

import ru.practicum.model.RequestDto;
import ru.practicum.model.ResponseDto;

import java.util.List;

public interface StatsService {
    void saveHit(RequestDto requestDto);

    List<ResponseDto> getStats(String start, String end, List<String> uris, boolean unique);
}
