package ru.practicum.ewmservice.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.ewmservice.service.CommentService;

import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.ewmservice.util.PageFactory.createPageable;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentControllerAdmin {
    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(value = "commentId") Long commentId) {
        log.trace("Endpoint request: DELETE /admin/comments/{commentId}");
        log.debug("Param: path variable '{}'", commentId);
        commentService.deleteCommentFromAdmin(commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> searchComment(@RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                          @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) Integer size,
                                          @RequestParam String text) {
        log.trace("Endpoint request: GET /admin/comments");
        log.debug("Param: pageable from = '{}', size = '{}'", from, size);
        final Pageable pageable = createPageable(from, size, Sort.Direction.DESC, "createdDate");
        return commentService.searchCommentByText(text, pageable);
    }
}
