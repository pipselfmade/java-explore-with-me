package ru.practicum.ewmservice.mapper;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.input.NewCompilationDto;
import ru.practicum.dto.input.UpdateCompilationDto;
import ru.practicum.ewmservice.model.Compilation;

import static ru.practicum.ewmservice.mapper.EventMapper.toShortDtoList;

public class CompilationMapper {
    public static CompilationDto toDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(toShortDtoList(compilation.getEvents()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle()).build();
    }

    public static Compilation toModel(NewCompilationDto compilationDto) {
        return Compilation.builder()
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle()).build();
    }

    public static Compilation toModel(UpdateCompilationDto update) {
        return Compilation.builder()
                .id(update.getId())
                .pinned(update.getPinned())
                .title(update.getTitle()).build();
    }
}
