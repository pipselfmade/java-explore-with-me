package ru.practicum.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.dto.location.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    @NotBlank(message = "Annotation cannot be blank")
    @Length(max = 2000, min = 20, message = "Annotation cannot be min = 20 max = 200")
    private String annotation;

    @NotNull(message = "Id category cannot be null")
    @Positive(message = "Id category can be only positive")
    private Long category;

    @NotBlank
    @Length(max = 7000, min = 20, message = "Description cannot be length min 20 max 7000")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "Location cannot be null")
    private Location location;

    private Boolean paid = false;

    @PositiveOrZero(message = "Participant Limit can be only zero(unlimited) or positive")
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @NotNull(message = "Title cannot be null")
    @Length(min = 3, max = 120, message = "Title cannot be length min 3 max 120")
    private String title;
}
