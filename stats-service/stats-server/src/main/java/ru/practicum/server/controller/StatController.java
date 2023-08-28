package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.statistics.EndpointHitDto;
import ru.practicum.dto.statistics.ViewStatsDto;
import ru.practicum.server.service.StatService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService statService;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveStat(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("New POST /hit request");
        statService.saveStat(endpointHitDto);
    }

    @GetMapping("/hit")
    public List<EndpointHitDto> getAllStats() {
        log.info("New Get /hit request");
        return statService.getAll();
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@NotEmpty @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
                                       @NotEmpty @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("New GET /stats request");
        return statService.getStats(start, end, uris, unique);
    }
}
