package ru.practicum.ewmservice.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.input.EventRequestStatusUpdateRequest;
import ru.practicum.dto.input.NewEventDto;
import ru.practicum.dto.input.UpdateEventAdminRequest;
import ru.practicum.dto.input.UpdateEventUserRequest;
import ru.practicum.dto.request.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEventsByUserId(Long userId, Pageable pageable);

    EventFullDto createNewEvent(Long userId, NewEventDto eventDto);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEventByUsersIdAndEventIdFromUser(Long userId, Long eventId, UpdateEventUserRequest update);

    List<ParticipationRequestDto> getAllParticipationRequestsFromEventByOwner(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest update);

    List<EventFullDto> getAllEventForParamFromAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd, Pageable pageable);

    EventFullDto updateEventByEventIdFromAdmin(Long eventId, UpdateEventAdminRequest update);

    List<EventShortDto> getAllEventFromPublic(String text, List<Long> categories, Boolean paid,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              Boolean onlyAvailable, String sort, Pageable pageable, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);
}
