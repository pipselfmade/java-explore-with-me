package ru.practicum.ewmservice.controller.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.input.EventRequestStatusUpdateRequest;
import ru.practicum.dto.input.NewEventDto;
import ru.practicum.dto.input.UpdateEventUserRequest;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.ewmservice.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.ewmservice.util.PageFactory.createPageable;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventControllerUsers {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEventsByUserId(@PathVariable(value = "userId") @Min(1) Long userId,
                                                    @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                                    @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) Integer size) {
        log.trace("Endpoint request: GET /users/{userId}/events");
        log.debug("Param: user id = '{}', from = '{}', size = '{}'", userId, from, size);
        final Pageable pageable = createPageable(from, size, Sort.Direction.ASC, "id");
        return eventService.getEventsByUserId(userId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createNewEvent(@PathVariable(value = "userId") @Min(1) Long userId,
                                       @Valid @RequestBody NewEventDto input) {
        log.trace("Endpoint request: POST /users/{userId}/events");
        log.debug("Param: input body '{}'", input);
        return eventService.createNewEvent(userId, input);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getFullEventByOwner(@PathVariable(value = "userId") @Min(1) Long userId,
                                            @PathVariable(value = "eventId") @Min(1) Long eventId) {
        log.trace("Endpoint request: GET /users/{userId}/events/{eventId}");
        log.debug("Param: user id = '{}', event id = '{}'", userId, eventId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByOwner(@PathVariable(value = "userId") @Min(0) Long userId,
                                           @PathVariable(value = "eventId") @Min(0) Long eventId,
                                           @Valid @RequestBody UpdateEventUserRequest inputUpdate) {
        log.trace("Endpoint request: PATCH /users/{userId}/events/{eventId}");
        log.debug("Param: user id = '{}', event id = '{}', request body = '{}'", userId, eventId, inputUpdate);
        return eventService.updateEventByUsersIdAndEventIdFromUser(userId, eventId, inputUpdate);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllRequestByEventFromOwner(@PathVariable(value = "userId") @Min(1) Long userId,
                                                                       @PathVariable(value = "eventId") @Min(1) Long eventId) {
        log.trace("Endpoint request: GET /users/{userId}/events/{eventId}/requests");
        log.debug("Param: user id = '{}', event id = '{}'", userId, eventId);
        return eventService.getAllParticipationRequestsFromEventByOwner(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateStatusRequestFromOwner(@PathVariable(value = "userId") @Min(1) Long userId,
                                                                       @PathVariable(value = "eventId") @Min(1) Long eventId,
                                                                       @RequestBody EventRequestStatusUpdateRequest inputUpdate) {
        log.trace("Endpoint request: PATCH /users/{userId}/events/{eventId}/requests");
        log.debug("Param: user id = '{}', event id = '{}', object update = '{}'", userId, eventId, inputUpdate);
        return eventService.updateStatusRequests(userId, eventId, inputUpdate);
    }
}
