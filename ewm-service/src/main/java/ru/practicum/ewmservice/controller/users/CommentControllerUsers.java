package ru.practicum.ewmservice.controller.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.input.NewCommentDto;
import ru.practicum.ewmservice.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.ewmservice.util.PageFactory.createPageable;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentControllerUsers {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createNewComment(@PathVariable(value = "userId") Long userId,
                                       @Valid @RequestBody NewCommentDto input,
                                       @RequestParam(value = "eventId") Long eventId) {
        log.trace("Endpoint request: POST /users/{userId}/comments");
        log.debug("Param: user id = '{}', event id = '{}', input body = [{}]", userId, eventId, input);
        return commentService.createNewComment(userId, eventId, input);
    }

    @GetMapping
    public List<CommentDto> getAllCommentByUser(@PathVariable(value = "userId") Long userId,
                                                @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) Integer size) {
        log.trace("Endpoint request: GET /users/{userId}/comments");
        log.debug("Param: user id = '{}', from = '{}', size = '{}'", userId, from, size);
        final Pageable pageable = createPageable(from, size, Sort.Direction.DESC, "createdDate");
        return commentService.getAllCommentsFromUser(userId, pageable);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "commentId") Long commentId,
                                    @Valid @RequestBody NewCommentDto update) {
        log.trace("Endpoint request: PATH /users/{userId}/comments/{commentId}");
        log.debug("Param: user id = '{}', comment id = '{}', update = [{}]", userId, commentId, update);
        return commentService.updateComment(userId, commentId, update);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(value = "userId") Long userId,
                              @PathVariable(value = "commentId") Long commentId) {
        log.trace("Endpoint request: DELETE /users/{userId}/comments/{commentId}");
        log.debug("Param: user id = '{}', comment id = '{}'", userId, commentId);
        commentService.deleteCommentFromOwner(userId, commentId);
    }
}
