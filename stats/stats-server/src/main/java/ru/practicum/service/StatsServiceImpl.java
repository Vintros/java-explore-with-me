package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.model.RequestDto;
import ru.practicum.model.ResponseDto;
import ru.practicum.storage.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void saveHit(RequestDto requestDto) {
        repository.save(requestDto);
    }

    @Override
    public List<ResponseDto> getStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, dtf);
        LocalDateTime endTime = LocalDateTime.parse(end, dtf);
        if (uris.isEmpty()) {
            return new ArrayList<>();
        }
        if (unique) {
            return repository.getStatsUnique(startTime, endTime, uris);
        } else {
            return repository.getStats(startTime, endTime, uris);
        }
    }
}
