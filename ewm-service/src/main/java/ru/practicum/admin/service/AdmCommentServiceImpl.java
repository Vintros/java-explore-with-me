package ru.practicum.admin.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.storage.AdmCommentRepository;
import ru.practicum.common.dto.CommentFullDto;
import ru.practicum.common.exceptions.EntityNotFoundException;
import ru.practicum.common.exceptions.IncorrectRequestException;
import ru.practicum.common.mapper.CommentMapper;
import ru.practicum.common.model.Comment;
import ru.practicum.common.model.QComment;
import ru.practicum.common.util.RequestParamsForComments;
import ru.practicum.common.util.StateComment;
import ru.practicum.common.util.StateCommentAction;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdmCommentServiceImpl implements AdmCommentService {

    private final AdmCommentRepository commentRepository;
    private final CommentMapper mapper;

    @Override
    public List<CommentFullDto> getComments(RequestParamsForComments params, Pageable pageable) {
        QComment qComment = QComment.comment;
        BooleanExpression predicate = qComment.isNotNull();

        if (params.getUserId() != null) {
            predicate = predicate.and(qComment.author.id.eq(params.getUserId()));
        }
        if (params.getState() != null) {
            predicate = predicate.and(qComment.state.eq(params.getState()));
        }
        if (params.getRangeStart() != null) {
            predicate = predicate.and(qComment.created.after(params.getRangeStart()));
        }
        if (params.getRangeEnd() != null) {
            predicate = predicate.and(qComment.created.before(params.getRangeEnd()));
        }
        List<Comment> comments = commentRepository.findAll(predicate, pageable).toList();
        return mapper.convertAllToCommentFullDto(comments);
    }

    @Override
    @Transactional
    public List<CommentFullDto> inspectComments(List<Long> commentsId, StateCommentAction action) {
        if (commentsId.isEmpty()) {
            throw new IncorrectRequestException("List of comments id is empty");
        }
        List<Comment> comments = commentRepository.findAllById(commentsId);
        if (comments.size() != commentsId.size()) {
            throw new EntityNotFoundException("some comments not founded");
        }
        switch (action) {
            case TO_DELETE:
                for (Comment comment : comments) {
                    comment.setState(StateComment.DELETED);
                }
                commentRepository.deleteAllById(commentsId);
                break;
            case TO_RESTORE:
                for (Comment comment : comments) {
                    comment.setState(StateComment.PUBLISHED);
                }
                commentRepository.saveAll(comments);
        }
        return mapper.convertAllToCommentFullDto(comments);
    }

    @Override
    public void deleteComment(Long eventId, Long commentId) {

        if (!commentRepository.existsByIdAndEvent_Id(commentId, eventId)) {
            throw new EntityNotFoundException("Event or comment not found");
        }
        commentRepository.deleteById(commentId);
    }

}
