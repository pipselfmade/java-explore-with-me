package ru.practicum.ewmservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.input.NewCommentDto;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.exception.ValidatedException;
import ru.practicum.ewmservice.mapper.CommentMapper;
import ru.practicum.ewmservice.model.Comment;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.model.User;
import ru.practicum.ewmservice.repository.CommentRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.UserRepository;
import ru.practicum.ewmservice.service.CommentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewmservice.mapper.CommentMapper.toDto;
import static ru.practicum.ewmservice.mapper.CommentMapper.toModel;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto createNewComment(Long userId, Long eventId, NewCommentDto input) {
        final LocalDateTime createdOn = LocalDateTime.now();
        final User author = getUserById(userId);
        final Event event = getEventById(eventId);
        final Comment comment = toModel(input);

        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreatedDate(createdOn);

        log.debug("Comment received [{}]", comment);
        final Comment commentAfterSave = commentRepository.save(comment);
        log.debug("Comment after save [{}]", commentAfterSave);
        return toDto(commentAfterSave);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, NewCommentDto update) {
        isUserExists(userId);
        final Comment oldComment = getCommentById(commentId);
        isUserOwner(oldComment, userId);

        String updateComment = update.getText();
        log.debug("Text update comment: '{}'", updateComment);

        if (updateComment != null && !updateComment.isBlank()) {
            oldComment.setText(updateComment);
        }

        return updateComment == null ? null : toDto(commentRepository.save(oldComment));
    }

    @Override
    public List<CommentDto> getAllCommentsFromEvent(Long eventId, Pageable pageable) {
        isEventExists(eventId);
        return commentRepository.findAllByEventId(eventId, pageable).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsFromUser(Long userId, Pageable pageable) {
        isUserExists(userId);
        return commentRepository.findAllByAuthorId(userId, pageable).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCommentFromOwner(Long userId, Long commentId) {
        isUserExists(userId);
        commentRepository.deleteByIdAndAuthorId(commentId, userId);
        log.debug("Comment delete ? {}", !commentRepository.existsById(commentId));
    }

    @Override
    @Transactional
    public void deleteCommentFromAdmin(Long commentId) {
        commentRepository.deleteById(commentId);
        log.debug("Comment delete ? {}", !commentRepository.existsById(commentId));
    }

    @Override
    public List<CommentDto> searchCommentByText(String text, Pageable pageable) {
        List<Comment> comments = commentRepository.findAllByTextContainingIgnoreCase(text, pageable);
        return text.isBlank() ?
                new ArrayList<>() :
                commentRepository.findAllByTextContainingIgnoreCase(text, pageable).stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id = '%d' not found", userId)));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id = '%d' not found", eventId)));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException(String.format("Comment with id = '%d' not found", commentId)));
    }

    private void isUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id = '%d' not found", userId));
        }
    }

    private void isEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id = '%d' not found", eventId));
        }
    }

    private void isUserOwner(Comment comment, Long userId) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidatedException("Update comment can only owner");
        }
    }
}
