package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.RequestDto;
import ru.practicum.model.ResponseDto;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveHit(@RequestBody RequestDto requestDto) {
        service.saveHit(requestDto);
    }

    @GetMapping("/stats")
    public List<ResponseDto> getStats(@RequestParam String start, @RequestParam String end,
                                @RequestParam(defaultValue = "") List<String> uris,
                                @RequestParam(defaultValue = "false") boolean unique) {
        return service.getStats(start, end, uris, unique);
    }

}
