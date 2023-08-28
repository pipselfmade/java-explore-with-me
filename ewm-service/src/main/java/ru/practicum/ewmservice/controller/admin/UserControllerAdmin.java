package ru.practicum.ewmservice.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.input.NewUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.ewmservice.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.ewmservice.util.PageFactory.createPageable;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserControllerAdmin {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsersInfoByIds(@RequestParam(required = false) List<Long> ids,
                                           @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.trace("Endpoint request: GET admin/users");
        log.debug("Param: array ids = '{}', from = '{}', size = '{}'", ids, from, size);
        final Pageable pageable = createPageable(from, size, Sort.Direction.ASC, "id");
        return userService.findUserById(ids, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto created(@Valid @RequestBody NewUserDto input) {
        log.trace("Endpoint request: POST admin/users");
        log.debug("Param: input body '{}'", input);
        return userService.created(input);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(1) Long userId) {
        log.trace("Endpoint request: DELETE admin/users");
        log.debug("Param: Path variable '{}'", userId);
        userService.delete(userId);
    }
}
