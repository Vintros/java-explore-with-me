package ru.practicum.personal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.storage.AdmUserRepository;
import ru.practicum.common.dto.CommentDto;
import ru.practicum.common.dto.CommentDtoRequest;
import ru.practicum.common.exceptions.EntityNoAccessException;
import ru.practicum.common.exceptions.EntityNotFoundException;
import ru.practicum.common.mapper.CommentMapper;
import ru.practicum.common.model.Comment;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.User;
import ru.practicum.common.util.StateComment;
import ru.practicum.common.util.StateRequest;
import ru.practicum.personal.storage.PersonalCommentRepository;
import ru.practicum.personal.storage.PersonalEventRepository;
import ru.practicum.personal.storage.PersonalRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalCommentServiceImpl implements PersonalCommentService {

    private final PersonalCommentRepository commentRepository;
    private final PersonalEventRepository eventRepository;
    private final AdmUserRepository userRepository;
    private final PersonalRequestRepository requestRepository;
    private final CommentMapper mapper;

    @Override
    public CommentDto createComment(Long userId, Long eventId, CommentDtoRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("event not found"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new EntityNoAccessException("You can't comment on your own event");
        }
        List<Comment> oldComments = event.getComments().stream()
                .filter(c -> c.getAuthor().getId().equals(userId))
                .collect(Collectors.toList());
        boolean isParticipant = requestRepository.existsByEvent_idAndRequester_IdAndStatusIs(
                eventId, userId, StateRequest.CONFIRMED);
        if (oldComments.size() >= 2
                || (oldComments.size() == 1 && LocalDateTime.now().isBefore(event.getEventDate()))
                || (!isParticipant && LocalDateTime.now().isAfter(event.getEventDate()))) {
            throw new EntityNoAccessException("You can't comment on an event");
        }
        Comment comment = mapper.convertToComment(request, user, event);
        Comment savedComment = commentRepository.save(comment);
        return mapper.convertToCommentDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, CommentDtoRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event not found"));
        Comment comment = event.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new EntityNoAccessException("You can't update this comment");
        }
        if (LocalDateTime.now().isBefore(event.getEventDate())) {
            comment.setText(request.getText());
        } else {
            boolean isParticipant = requestRepository.existsByEvent_idAndRequester_IdAndStatusIs(
                    eventId, userId, StateRequest.CONFIRMED);
            if (isParticipant && comment.getCreated().isAfter(event.getEventDate())) {
                comment.setText(request.getText());
            } else {
                throw new EntityNoAccessException("You can't update this comment");
            }
        }
        Comment savedComment = commentRepository.save(comment);
        return mapper.convertToCommentDto(savedComment);
    }

    @Override
    public void sendCommentToCheckByEventOwner(Long userId, Long eventId, Long commentId) {
        Comment comment = commentRepository.findByIdAndEvent_Id(commentId, eventId).orElseThrow(
                () -> new EntityNotFoundException("Event or comment not found"));
        if (!comment.getEvent().getInitiator().getId().equals(userId)) {
            throw new EntityNoAccessException("You can't submit this comment to inspection, " +
                    "you are not the owner of the event");
        }
        comment.setState(StateComment.ON_INSPECTION);
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        Comment comment = commentRepository.findByIdAndEvent_Id(commentId, eventId).orElseThrow(
                () -> new EntityNotFoundException("Comment not found"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new EntityNoAccessException("You can't delete this comment");
        }
        commentRepository.deleteById(commentId);
    }
}
