package ru.practicum.ewmservice.controller.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.ewmservice.service.CategoryService;

import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.ewmservice.util.PageFactory.createPageable;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryControllerPublic {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAll(@RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                    @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.trace("Endpoint request: GET /categories");
        log.debug("Param: from = '{}', size = '{}'", from, size);
        final Pageable pageable = createPageable(from, size, Sort.Direction.ASC, "id");
        return categoryService.getAll(pageable);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getById(@PathVariable @Min(1) Long catId) {
        log.trace("Endpoint request: GET /categories/{catId}");
        log.debug("Param: categoryId = '{}'", catId);
        return categoryService.getById(catId);
    }
}