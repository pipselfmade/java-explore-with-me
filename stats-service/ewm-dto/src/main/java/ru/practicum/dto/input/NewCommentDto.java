package ru.practicum.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCommentDto {
    @NotBlank(message = "comment cannot blank")
    @Length(min = 1, max = 7000, message = "comment length max = 7000, min = 1")
    private String text;
    @CreationTimestamp
    private LocalDateTime created;
}
