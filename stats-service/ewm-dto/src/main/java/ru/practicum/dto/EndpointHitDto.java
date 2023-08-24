package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointHitDto {
    private Long id;

    @NotEmpty(message = "Name app is empty")
    private String app;

    @NotEmpty(message = "Uri is empty")
     private String uri;

    @NotEmpty(message = "IP is empty")
    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Timestamp is null")
    private LocalDateTime timestamp;
}
