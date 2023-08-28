package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.statistics.EndpointHitDto;
import ru.practicum.dto.statistics.ViewStatsDto;
import ru.practicum.server.mapper.EndpointHitMapper;
import ru.practicum.server.repository.StatRepository;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.server.mapper.EndpointHitMapper.toObject;
import static ru.practicum.server.mapper.ViewStatsMapper.toViewStatsDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    public void saveStat(EndpointHitDto endpointHitDto) {
        log.debug("EndpointHit with id {} saved", endpointHitDto.getId());
        statRepository.save(toObject(endpointHitDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            log.info("Error detected: start time {}, end time {}", start, end);
            throw new InvalidParameterException("End time no be after start time");
        }

        log.info("Get statistics from {} to {}, for URI's {}, IP unique: {}", start, end, uris, unique);

        if (uris != null && uris.size() > 0) {
            return unique ?
                    toViewStatsDto(statRepository.getAllByUrisAndUniqueIp(start, end, uris)) :
                    toViewStatsDto(statRepository.getAllByUris(start, end, uris));
        } else {
            return unique ?
                    toViewStatsDto(statRepository.getAllByUniqueIp(start, end)) :
                    toViewStatsDto(statRepository.getAll(start, end));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EndpointHitDto> getAll() {
        log.debug("All endpoints returned");
        return statRepository.findAll().stream().map(EndpointHitMapper::toDto).collect(Collectors.toList());
    }
}
