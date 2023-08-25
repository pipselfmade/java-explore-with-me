package ru.practicum.dto.input;

import lombok.*;
import ru.practicum.dto.enums.RequestStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
