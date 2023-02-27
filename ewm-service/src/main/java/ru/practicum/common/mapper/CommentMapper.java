package ru.practicum.common.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.common.dto.CommentDto;
import ru.practicum.common.dto.CommentDtoRequest;
import ru.practicum.common.dto.CommentFullDto;
import ru.practicum.common.model.Comment;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.User;
import ru.practicum.common.util.StateComment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentMapper {

    public Comment convertToComment(CommentDtoRequest request, User user, Event event) {
        return new Comment(
                null,
                user,
                event,
                request.getText(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                StateComment.PUBLISHED
        );
    }

    public CommentDto convertToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getAuthor().getName(),
                comment.getText(),
                comment.getCreated()
        );
    }

    public CommentFullDto convertToCommentFullDto(Comment comment) {
        return new CommentFullDto(
                comment.getId(),
                comment.getAuthor(),
                comment.getEvent().getId(),
                comment.getText(),
                comment.getCreated(),
                comment.getState()
        );
    }

    public List<CommentFullDto> convertAllToCommentFullDto(List<Comment> comments) {
        return comments.stream()
                .map(this::convertToCommentFullDto)
                .collect(Collectors.toList());
    }

    public List<CommentDto> convertAllToCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());
    }
}
