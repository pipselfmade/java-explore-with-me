package ru.practicum.ewmservice.controller.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.ewmservice.service.CompilationService;

import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.ewmservice.util.PageFactory.createPageable;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationControllerPublic {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(required = false) boolean pinned,
                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                       @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.trace("Endpoint request: GET /compilations");
        final Pageable pageable = createPageable(from, size, Sort.Direction.ASC, "id");
        return compilationService.getAll(pinned, pageable);
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable Long compId) {
        log.trace("Endpoint request: GET /compilations");
        CompilationDto compilationDto = compilationService.getById(compId);
        return compilationService.getById(compId);
    }
}
