package ru.practicum.admin.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.common.dto.CommentFullDto;
import ru.practicum.common.util.RequestParamsForComments;
import ru.practicum.common.util.StateCommentAction;

import java.util.List;

public interface AdmCommentService {
    void deleteComment(Long eventId, Long commentId);

    List<CommentFullDto> getComments(RequestParamsForComments params, Pageable pageable);

    List<CommentFullDto> inspectComments(List<Long> commentsId, StateCommentAction action);
}
