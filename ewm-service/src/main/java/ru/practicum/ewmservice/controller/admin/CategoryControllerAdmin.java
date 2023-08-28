package ru.practicum.ewmservice.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.input.NewCategoryDto;
import ru.practicum.ewmservice.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryControllerAdmin {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto created(@Valid @RequestBody NewCategoryDto categoryDto) {
        log.trace("Endpoint request: POST admin/categories");
        log.debug("Param: input body '{}'", categoryDto);
        return categoryService.create(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(1) Long catId) {
        log.trace("Endpoint request: DELETE admin/categories/{catId}");
        log.debug("Param: Path variable '{}'", catId);
        categoryService.delete(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@PathVariable @Min(1) Long catId,
                              @Valid @RequestBody NewCategoryDto categoryDto) {
        log.trace("Endpoint request: PATCH admin/categories/{catId}");
        log.debug("Param: Path variable '{}', Param: input body '{}'", catId, categoryDto);
        return categoryService.update(catId, categoryDto);
    }
}
