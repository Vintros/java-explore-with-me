package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveHit(@RequestBody RequestHitDto requestHitDto) {
        log.info("Save hit: {}", requestHitDto);
        service.saveHit(requestHitDto);
    }

    @GetMapping("/stats")
    public List<ResponseHitDto> getStats(@RequestParam String start, @RequestParam String end,
                                         @RequestParam(defaultValue = "") List<String> uris,
                                         @RequestParam(defaultValue = "false", required = false) boolean unique) {
        log.info("Get stats by uris: {}", uris);
        return service.getStats(start, end, uris, unique);
    }

}
