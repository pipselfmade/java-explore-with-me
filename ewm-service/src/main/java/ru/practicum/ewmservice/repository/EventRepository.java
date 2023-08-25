package ru.practicum.ewmservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.enums.EventStatus;
import ru.practicum.ewmservice.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByInitiatorIdAndId(Long userId, Long eventId);

    Boolean existsByInitiatorIdAndId(Long userId, Long eventId);

    List<Event> findAll(Specification<Event> specification, Pageable pageable);

    Optional<Event> findByIdAndEventStatus(Long eventId, EventStatus status);
}
