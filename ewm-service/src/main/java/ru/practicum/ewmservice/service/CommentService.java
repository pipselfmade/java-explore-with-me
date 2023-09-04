package ru.practicum.ewmservice.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.input.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createNewComment(Long userId, Long eventId, NewCommentDto input);

    CommentDto updateComment(Long userId, Long commentId, NewCommentDto update);

    List<CommentDto> getAllCommentsFromEvent(Long eventId, Pageable pageable);

    List<CommentDto> getAllCommentsFromUser(Long userId, Pageable pageable);

    void deleteCommentFromOwner(Long userId, Long commentId);

    void deleteCommentFromAdmin(Long commentId);

    List<CommentDto> searchCommentByText(String searchParam, Pageable pageable);
}
