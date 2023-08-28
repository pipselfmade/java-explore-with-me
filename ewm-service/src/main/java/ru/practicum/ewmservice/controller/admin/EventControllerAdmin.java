package ru.practicum.ewmservice.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.input.UpdateEventAdminRequest;
import ru.practicum.ewmservice.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewmservice.util.PageFactory.createPageable;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventControllerAdmin {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> searchEvents(@RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false) List<String> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.trace("Endpoint request: GET /admin/events");
        log.debug("Param: search id user = '{}', search state events = '{}', search id categories = '{}', range start = '{}', " +
                "range end = '{}', pageable from = '{}', pageable size = '{}'", users, states, categories, rangeStart, rangeEnd, from, size);
        final Pageable pageable = createPageable(from, size, Sort.Direction.ASC, "id");
        return eventService.getAllEventForParamFromAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable @Min(1) Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest inputUpdate) {
        log.trace("Endpoint request: PATCH /admin/events/{eventId}");
        log.debug("Param: event id = '{}', object update = '{}'", eventId, inputUpdate);
        return eventService.updateEventByEventIdFromAdmin(eventId, inputUpdate);
    }
}
