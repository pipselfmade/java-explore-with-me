package ru.practicum.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCategoryDto {
    @NotNull(message = "Name category is null")
    @NotBlank(message = "Name category cannot be blank")
    @Length(max = 50, min = 1, message = "Name category length max = 50 min = 1")
    private String name;
}
