package ru.practicum.ewmservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.ewmservice.model.Request;

@UtilityClass
public class RequestMapper {
    public static ParticipationRequestDto toDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .requester(request.getId())
                .status(request.getStatus()).build();
    }

    public static Request toModel(ParticipationRequestDto participationRequestDto) {
        return Request.builder()
                .id(participationRequestDto.getId())
                //.event(null)
                .created(participationRequestDto.getCreated())
                .requester(null)
                .status(participationRequestDto.getStatus()).build();
    }
}
