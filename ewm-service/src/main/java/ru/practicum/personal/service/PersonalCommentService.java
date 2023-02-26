package ru.practicum.personal.service;

import ru.practicum.common.dto.CommentDto;
import ru.practicum.common.dto.CommentDtoRequest;

public interface PersonalCommentService {
    CommentDto createComment(Long userId, Long eventId, CommentDtoRequest request);

    void deleteComment(Long userId, Long eventId, Long commentId);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, CommentDtoRequest request);

    void sendCommentToCheckByEventOwner(Long userId, Long eventId, Long commentId);
}
