package ru.practicum.ewmservice.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.enums.EventStatus;
import ru.practicum.dto.enums.RequestStatus;
import ru.practicum.dto.enums.StateAction;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.input.EventRequestStatusUpdateRequest;
import ru.practicum.dto.input.NewEventDto;
import ru.practicum.dto.input.UpdateEventAdminRequest;
import ru.practicum.dto.input.UpdateEventUserRequest;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.statistics.EndpointHitDto;
import ru.practicum.dto.statistics.ViewStatsDto;
import ru.practicum.ewmservice.dto.CaseUpdatedStatusDto;
import ru.practicum.ewmservice.exception.IncorrectParametersException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.exception.TimeValidationException;
import ru.practicum.ewmservice.exception.ValidatedException;
import ru.practicum.ewmservice.mapper.EventMapper;
import ru.practicum.ewmservice.mapper.RequestMapper;
import ru.practicum.ewmservice.model.Category;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.model.Request;
import ru.practicum.ewmservice.model.User;
import ru.practicum.ewmservice.repository.CategoryRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.RequestRepository;
import ru.practicum.ewmservice.repository.UserRepository;
import ru.practicum.ewmservice.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Pageable pageable) {
        isExistsUser(userId);
        final List<Event> result = eventRepository.findAllByInitiatorId(userId, pageable);
        log.debug("Finded '{}' events by user id = '{}', sort by = '{}'", result.size(), userId, pageable.getSort());
        return result.stream().map(EventMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto createNewEvent(Long userId, NewEventDto eventDto) {
        final LocalDateTime createdOn = LocalDateTime.now();
        final User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id = '" + userId + "' not found!"));
        isBeforeTwoHours(eventDto.getEventDate());
        final Category category = getCategoryById(eventDto.getCategory());
        final Event event = EventMapper.toModel(eventDto);
        log.debug("Convert to event. Event after : [{}], category received: [{}] and User received: [{}]",
                event.toString(), category.toString(), user.toString());
        event.setCategory(category);
        event.setInitiator(user);
        event.setEventStatus(EventStatus.PENDING);
        event.setCreatedDate(createdOn);
        log.debug("Event set created date and category and initiator '{}'", event.getCreatedDate().toString());
        event.setConfirmedRequests(0);
        event.setViews(0);
        log.debug("Event set view adn confirmed requests '0'");
        final Event eventSaved = eventRepository.save(event);
        log.debug("Event saved. Event after save - [{}]", eventSaved.toString());
        return EventMapper.toFullDto(eventSaved);
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        isExistsUser(userId);
        final Event event = getEvenByInitiatorAndEventId(userId, eventId);
        log.debug("Event received [{}]", event.toString());
        return EventMapper.toFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUsersIdAndEventIdFromUser(Long userId, Long eventId, UpdateEventUserRequest update) {
        isExistsUser(userId);
        int sizeUpdate = 0;
        final Event oldEvent = getEvenByInitiatorAndEventId(userId, eventId);

        if (update.getEventDate() != null) {
            final LocalDateTime newDate = update.getEventDate();
            isBeforeTwoHours(newDate);
            oldEvent.setEventDate(newDate);
            sizeUpdate++;
            log.debug("Event date update '{}'", newDate);
        }
        if (!oldEvent.getInitiator().getId().equals(userId)) {
            throw new ValidatedException("Only owner can update event");
        }
        if (oldEvent.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ValidatedException("Only reject or cancelled can update event");
        }
        if (update.getAnnotation() != null && !update.getAnnotation().isBlank()) {
            oldEvent.setAnnotation(update.getAnnotation());
            sizeUpdate++;
            log.debug("Annotation updated '{}'", oldEvent.getAnnotation());
        }
        if (update.getCategory() != null) {
            final Category category = getCategoryById(update.getCategory());
            log.debug("Category received");
            oldEvent.setCategory(category);
            sizeUpdate++;
            log.debug("Category update [{}]", category.toString());
        }
        if (update.getDescription() != null) {
            oldEvent.setDescription(update.getDescription());
            sizeUpdate++;
            log.debug("Description update '{}'", oldEvent.getDescription());
        }
        if (update.getLocation() != null) {
            oldEvent.setLocation(update.getLocation());
            sizeUpdate++;
            log.debug("Location update [{}]", oldEvent.getLocation());
        }
        if (update.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(update.getParticipantLimit());
            sizeUpdate++;
            log.debug("Participant limit update '{}'", oldEvent.getParticipantLimit());
        }
        if (update.getRequestModeration() != null) {
            oldEvent.setRequestModeration(update.getRequestModeration());
            sizeUpdate++;
            log.debug("Request moderation update '{}'", oldEvent.getRequestModeration());
        }
        if (update.getStateAction() != null) {
            switch (update.getStateAction()) {
                case SEND_TO_REVIEW:
                    oldEvent.setEventStatus(EventStatus.PENDING);
                    sizeUpdate++;
                    log.debug("State update '{}'", oldEvent.getEventStatus());
                    break;
                case CANCEL_REVIEW:
                    oldEvent.setEventStatus(EventStatus.CANCELED);
                    sizeUpdate++;
                    log.debug("State update '{}'", oldEvent.getEventStatus());
                    break;
            }
        }
        if (update.getTitle() != null) {
            oldEvent.setTitle(update.getTitle());
            sizeUpdate++;
            log.debug("Title update '{}'", oldEvent.getTitle());
        }

        Event eventAfterUpdate = null;

        if (sizeUpdate > 0) {
            eventAfterUpdate = eventRepository.save(oldEvent);
        }

        log.debug("Event update? if null no new data: [{}]", eventAfterUpdate);
        return eventAfterUpdate != null ? EventMapper.toFullDto(eventAfterUpdate) : null;
    }

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestsFromEventByOwner(Long userId, Long eventId) {
        isExistsUser(userId);
        isUserInitiatedEvent(userId, eventId);
        final List<Request> requests = requestRepository.findAllByEventId(eventId);
        log.debug("Number of requests found '{}'", requests.size());
        return requests.stream().map(RequestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatusRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest update) {
        isExistsUser(userId);
        final Event event = getEvenByInitiatorAndEventId(userId, eventId);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ValidatedException("This event does not require confirmation of requests");
        }

        final RequestStatus status = update.getStatus();

        switch (status) {
            case CONFIRMED:
                return updateConfirmedStatusRequest(event, eventId, update);
            case REJECTED:
                return updateRejectedStatusRequest(event, update);
            default:
                throw new IncorrectParametersException("Incorrect status " + status);
        }
    }

    private EventRequestStatusUpdateResult updateConfirmedStatusRequest(Event event,
                                                                        Long eventId,
                                                                        EventRequestStatusUpdateRequest update) {
        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ValidatedException("Participant limit is full");
        }

        final CaseUpdatedStatusDto updatedStatusConfirmed = statusHandler(event, CaseUpdatedStatusDto.builder()
                .idsFromUpdateStatus(update.getRequestIds()).build(), RequestStatus.CONFIRMED);
        final List<Request> confirmedRequests = requestRepository.findAllById(updatedStatusConfirmed.getProcessedIds());
        log.debug("Confirmed requests '{};", confirmedRequests.size());
        List<Request> rejectedRequests = new ArrayList<>();

        if (updatedStatusConfirmed.getIdsFromUpdateStatus().size() != 0) {
            List<Long> ids = updatedStatusConfirmed.getIdsFromUpdateStatus();
            rejectedRequests = rejectOtherRequest(ids, eventId);
        }

        log.debug("Reject requests '{}'", rejectedRequests.size());
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream().map(RequestMapper::toDto).collect(Collectors.toList()))
                .rejectedRequests(rejectedRequests.stream().map(RequestMapper::toDto).collect(Collectors.toList())).build();
    }

    private EventRequestStatusUpdateResult updateRejectedStatusRequest(Event event, EventRequestStatusUpdateRequest update) {
        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ValidatedException("Participant limit is full");
        }

        final CaseUpdatedStatusDto updatedStatusReject = statusHandler(event, CaseUpdatedStatusDto.builder()
                .idsFromUpdateStatus(update.getRequestIds()).build(), RequestStatus.REJECTED);
        List<Request> rejectRequest = requestRepository.findAllById(updatedStatusReject.getProcessedIds());
        log.debug("Reject requests '{}'", rejectRequest.size());

        return EventRequestStatusUpdateResult.builder()
                .rejectedRequests(rejectRequest.stream().map(RequestMapper::toDto).collect(Collectors.toList())).build();
    }

    private List<Request> rejectOtherRequest(List<Long> ids, Long eventId) {
        List<Request> rejectedRequests = new ArrayList<>();

        requestRepository.rejectOtherRequest(ids);

        rejectedRequests = requestRepository.findAllById(ids);
        log.debug("Reject requests '{}'", rejectedRequests.size());
        return rejectedRequests;
    }

    @Override
    public List<EventFullDto> getAllEventForParamFromAdmin(List<Long> users,
                                                           List<String> states,
                                                           List<Long> categories,
                                                           LocalDateTime rangeStart,
                                                           LocalDateTime rangeEnd,
                                                           Pageable pageable) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new IncorrectParametersException("Time start cannot after time end");
        }

        Specification<Event> specification = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("initiator").get("id").in(users));
            log.debug("Specification update add users [{}]", specification.toString());
        }
        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("eventStatus").as(String.class).in(states));
            log.debug("Specification update add states [{}]", specification.toString());
        }
        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("category").get("id").in(categories));
            log.debug("Specification update add categories [{}]", specification.toString());
        }
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            log.debug("Specification update add end time [{}]", specification.toString());
        }
        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            log.debug("Specification update update add start rime [{}]", specification.toString());
        }

        List<Event> events = eventRepository.findAll(specification, pageable);
        log.debug("found by specification {} events {}", specification, events.size());
        return events.stream().map(EventMapper::toFullDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByEventIdFromAdmin(Long eventId, UpdateEventAdminRequest update) {
        final Event oldEvent = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id = '" + eventId + "' not found"));

        if (oldEvent.getEventStatus().equals(EventStatus.PUBLISHED) || oldEvent.getEventStatus().equals(EventStatus.CANCELED)) {
            throw new ValidatedException("Only pending event can be changed");
        }

        int countUpdate = 0;

        if (update.getAnnotation() != null && !update.getAnnotation().isBlank()) {
            if (update.getAnnotation().length() < 20 || update.getAnnotation().length() > 2000) {
                throw new IncorrectParametersException("incorrect length of the annotation parameter");
            } else {
                oldEvent.setAnnotation(update.getAnnotation());
            }
        }
        if (update.getCategory() != null) {
            final Category category = getCategoryById(update.getCategory());
            oldEvent.setCategory(category);
            countUpdate = 1;
        }
        if (update.getDescription() != null && !update.getDescription().isEmpty()) {
            oldEvent.setDescription(update.getDescription());
            countUpdate = 1;
        }
        if (update.getEventDate() != null) {
            isBeforeTwoHours(update.getEventDate());
            oldEvent.setEventDate(update.getEventDate());
            countUpdate = 1;
        }
        if (update.getLocation() != null) {
            oldEvent.setLocation(update.getLocation());
            countUpdate = 1;
        }
        if (update.getPaid() != null) {
            oldEvent.setPaid(update.getPaid());
            countUpdate = 1;
        }
        if (update.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(update.getParticipantLimit());
            countUpdate = 1;
        }
        if (update.getRequestModeration() != null) {
            oldEvent.setRequestModeration(update.getRequestModeration());
            countUpdate = 1;
        }
        if (update.getStateAction() != null) {
            if (update.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                oldEvent.setEventStatus(EventStatus.PUBLISHED);
                countUpdate = 1;
            } else if (update.getStateAction().equals(StateAction.REJECT_EVENT)) {
                oldEvent.setEventStatus(EventStatus.CANCELED);
                countUpdate = 1;
            }
        }
        if (update.getTitle() != null && !update.getTitle().isBlank()) {
            oldEvent.setTitle(update.getTitle());
            countUpdate = 1;
        }

        Event eventAfterUpdate = null;

        if (countUpdate > 0) {
            eventAfterUpdate = eventRepository.save(oldEvent);
        }

        return eventAfterUpdate != null ? EventMapper.toFullDto(eventAfterUpdate) : null;
    }

    @Override
    public List<EventShortDto> getAllEventFromPublic(String text, List<Long> categories, Boolean paid,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Boolean onlyAvailable, String sort, Pageable pageable, HttpServletRequest request) {
        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new IncorrectParametersException("End time before start time");
            }
        }

        addStatistic(request);
        Specification<Event> specification = Specification.where(null);

        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    ));
        }
        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, () -> now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
        }
        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("eventStatus"), EventStatus.PUBLISHED));

        List<Event> resultEvents = eventRepository.findAll(specification, pageable);
        setViewsOfEvents(resultEvents);

        return resultEvents.stream().map(EventMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        final Event event = eventRepository.findByIdAndEventStatus(eventId, EventStatus.PUBLISHED).orElseThrow(
                () -> new NotFoundException("Event with id = '" + eventId + "' not found"));
        addStatistic(request);
        setViewsOfEvents(List.of(event));
        return EventMapper.toFullDto(event);
    }

    private CaseUpdatedStatusDto statusHandler(Event event, CaseUpdatedStatusDto caseUpdatedStatus, RequestStatus status) {
        Long eventId = event.getId();
        final List<Long> ids = caseUpdatedStatus.getIdsFromUpdateStatus();
        final int idsSize = caseUpdatedStatus.getIdsFromUpdateStatus().size();
        final List<Long> processedIds = new ArrayList<>();
        int freeRequest = event.getParticipantLimit() - event.getConfirmedRequests();

        for (int i = 0; i < idsSize; i++) {
            final Request request = getRequestOrThrow(eventId, ids.get(i));

            if (freeRequest == 0) {
                break;
            }

            log.debug("Request received [{}]", request);
            request.setStatus(status);
            log.debug("Status update '{}'", request.getStatus());
            requestRepository.save(request);
            log.debug("Request saved!");
            Long confirmedId = request.getId();
            processedIds.add(confirmedId);
            freeRequest--;
        }

        Integer confirmedRequestUpdate = event.getConfirmedRequests() + processedIds.size();
        event.setConfirmedRequests(confirmedRequestUpdate);
        eventRepository.save(event);
        caseUpdatedStatus.setIdsFromUpdateStatus(ids);
        caseUpdatedStatus.setProcessedIds(processedIds);
        return caseUpdatedStatus;
    }

    private void isExistsUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.debug("User with id = '{}' not found", userId);
            throw new NotFoundException("User with id = '" + userId + "' not found!");
        }

        log.debug("Check user exist - ok");
    }

    private void isBeforeTwoHours(LocalDateTime eventDateStarted) {
        final LocalDateTime twoHoursLater = LocalDateTime.now().plusHours(2);

        if (eventDateStarted.isBefore(twoHoursLater)) {
            log.debug("The event will start in less than 2 hours. Date start '{}'", eventDateStarted);
            throw new TimeValidationException("Time start event cannot be after two hours started event");
        }

        log.debug("Time started event is check - ok");
    }

    private Event getEvenByInitiatorAndEventId(Long userId, Long eventId) {
        return eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new NotFoundException("Event with id = '" + eventId + "' and/or initiator with id = '" + userId + "' not found"));
    }

    private Category getCategoryById(Long caId) {
        return categoryRepository.findById(caId).orElseThrow(
                () -> new NotFoundException("Category with id = '" + caId + "' not found"));
    }

    private void isUserInitiatedEvent(Long userId, Long eventId) {
        if (!eventRepository.existsByInitiatorIdAndId(userId, eventId)) {
            throw new ValidatedException("User did not create this event");
        }
        log.debug("Initiator check - ok");
    }

    private Request getRequestOrThrow(Long eventId, Long reqId) {
        return requestRepository.findByEventIdAndId(eventId, reqId).orElseThrow(
                () -> new NotFoundException("Request with id = '" + reqId + "' not found or with event id = '" + eventId + "'"));
    }

    private void addStatistic(HttpServletRequest request) {
        String app = "ewm-service";
        statsClient.saveHit(EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private void setViewsOfEvents(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        ResponseEntity<Object> response = statsClient.getStatistics("2000-01-01 00:00:00", "2100-01-01 00:00:00", uris, false);

        final ObjectMapper mapper = new ObjectMapper();
        final List<ViewStatsDto> viewStatsList = mapper.convertValue(response.getBody(), new TypeReference<List<ViewStatsDto>>() {
        });

        for (Event event : events) {
            ViewStatsDto currentViewStats = viewStatsList.stream()
                    .filter(statsDto -> {
                        Long eventIdOfViewStats = Long.parseLong(statsDto.getUri().substring("/events/".length()));
                        return eventIdOfViewStats.equals(event.getId());
                    })
                    .findFirst()
                    .orElse(null);

            Long views = (currentViewStats != null) ? currentViewStats.getHits() : 0;
            event.setViews(views.intValue() + 1);
        }
        eventRepository.saveAll(events);
    }
}
