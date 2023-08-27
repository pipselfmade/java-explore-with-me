package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.enums.EventStatus;
import ru.practicum.dto.location.Location;
import ru.practicum.dto.user.UserShortDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class EventFullDto {
    private Long id;

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private LocalDateTime createdOn;
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventStatus state;

    private String title;

    private Integer views;
}
