package ru.practicum.ewmservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.input.NewCompilationDto;
import ru.practicum.dto.input.UpdateCompilationDto;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.mapper.CompilationMapper;
import ru.practicum.ewmservice.model.Compilation;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.repository.CompilationRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.service.CompilationService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto compilationDto) {
        final Compilation compilation = CompilationMapper.toModel(compilationDto);

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (compilationDto.getEvents() != null) {
            final List<Event> getEvent = eventRepository.findAllById(compilationDto.getEvents());
            compilation.setEvents(getEvent);
        } else {
            compilation.setEvents(new ArrayList<>());
        }

        final Compilation compilationAfterSave = compilationRepository.save(compilation);
        log.debug("Compilation save = [{}]", compilationAfterSave.toString());
        return CompilationMapper.toDto(compilationAfterSave);
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationDto update) {
        final Compilation compilation = getCompilationById(compId);

        if (update.getEvents() != null) {
            compilation.setEvents(update.getEvents().stream()
                    .flatMap(ids -> eventRepository.findAllById(Collections.singleton(ids))
                            .stream())
                    .collect(Collectors.toList()));
        }

        compilation.setPinned(update.getPinned() != null ? update.getPinned() : compilation.getPinned());
        compilation.setTitle(update.getTitle() != null ? update.getTitle() : compilation.getTitle());
        final Compilation compilationAfterSave = compilationRepository.save(compilation);
        return CompilationMapper.toDto(compilationAfterSave);
    }

    @Override
    public void delete(Long compId) {
        getCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAll(boolean pinned, Pageable pageable) {
        return compilationRepository.findAllByPinnedIs(pinned, pageable)
                .stream().map(CompilationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long compId) {
        return CompilationMapper.toDto(getCompilationById(compId));
    }

    private Compilation getCompilationById(Long comId) {
        return compilationRepository.findById(comId).orElseThrow(
                () -> new NotFoundException(String.format("Compilation with id = '%d' not found", comId)));
    }
}
