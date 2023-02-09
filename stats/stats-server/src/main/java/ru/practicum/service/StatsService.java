package ru.practicum.service;

import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ResponseHitDto;

import java.util.List;

public interface StatsService {
    void saveHit(RequestHitDto requestHitDto);

    List<ResponseHitDto> getStats(String start, String end, List<String> uris, boolean unique);
}
