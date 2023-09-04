package ru.practicum.ewmservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.input.NewCommentDto;
import ru.practicum.ewmservice.model.Comment;

import java.time.LocalDateTime;

import static ru.practicum.ewmservice.mapper.UserMapper.toShortDto;

@UtilityClass
public class CommentMapper {
    public static Comment toModel(NewCommentDto input) {
        return Comment.builder()
                .createdDate(LocalDateTime.now())
                .text(input.getText()).build();

    }

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .event(EventMapper.toShortDto(comment.getEvent()))
                .author(toShortDto(comment.getAuthor()))
                .createdOn(comment.getCreatedDate())
                .text(comment.getText()).build();
    }
}
