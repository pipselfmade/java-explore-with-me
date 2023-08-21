package ru.practicum.server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.model.ViewStats;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ViewStatsMapper {
    public static ViewStats toObject(ViewStatsDto viewStatsDto) {
        return ViewStats.builder()
                .app(viewStatsDto.getApp())
                .uri(viewStatsDto.getUri())
                .hits(viewStatsDto.getHits()).build();
    }

    public static ViewStatsDto toDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits()).build();
    }

    public static List<ViewStatsDto> toViewStatsDto(List<ViewStats> viewStats) {
        return viewStats.stream().map(ViewStatsMapper::toDto).collect(Collectors.toList());
    }
}
