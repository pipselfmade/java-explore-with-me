package ru.practicum.ewmservice.controller.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.ewmservice.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestControllerUsers {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createNewRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                                    @Valid @RequestParam(name = "eventId") Long eventId) {
        log.trace("Endpoint request: POST users/{userId}/requests");
        log.debug("Param: user id '{}', event id '{}'", userId, eventId);
        return requestService.createdNewRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllRequests(@PathVariable(value = "userId") @Min(0) Long userId) {
        log.trace("Endpoint request: GET users/{userId}/requests");
        log.debug("Param: user id '{}'", userId);
        return requestService.getAllRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto canceledRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                                   @PathVariable(value = "requestId") @Min(0) Long requestId) {
        log.trace("Endpoint request: GET /users/{userId}/requests/{requestId}/cancel");
        log.debug("Param: user id '{}', request id '{}'", userId, requestId);
        return requestService.cancel(userId, requestId);
    }
}
