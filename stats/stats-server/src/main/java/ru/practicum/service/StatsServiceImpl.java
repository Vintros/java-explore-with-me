package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.mapper.Mapper;
import ru.practicum.model.ResponseHit;
import ru.practicum.storage.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;
    private final Mapper mapper;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void saveHit(RequestHitDto requestHitDto) {
        repository.save(mapper.convertToRequestHit(requestHitDto));
    }

    @Override
    public List<ResponseHitDto> getStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, dtf);
        LocalDateTime endTime = LocalDateTime.parse(end, dtf);
        List<ResponseHit> hits;
        if (uris.isEmpty()) {
            return new ArrayList<>();
        }
        if (unique) {
            hits = repository.getStatsUnique(startTime, endTime, uris);
        } else {
            hits = repository.getStats(startTime, endTime, uris);
        }
        return mapper.convertAllToResponseHitDto(hits);
    }
}
