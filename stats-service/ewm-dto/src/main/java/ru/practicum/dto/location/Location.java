package ru.practicum.dto.location;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
    private Double lat;
    private Double lon;
}
