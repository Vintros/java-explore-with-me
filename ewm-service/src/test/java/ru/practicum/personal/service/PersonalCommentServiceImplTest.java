package ru.practicum.personal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.admin.storage.AdmUserRepository;
import ru.practicum.common.dto.CommentDto;
import ru.practicum.common.dto.CommentDtoRequest;
import ru.practicum.common.exceptions.EntityNoAccessException;
import ru.practicum.common.mapper.CommentMapper;
import ru.practicum.common.model.*;
import ru.practicum.common.util.StateComment;
import ru.practicum.common.util.StateRequest;
import ru.practicum.personal.storage.PersonalCommentRepository;
import ru.practicum.personal.storage.PersonalEventRepository;
import ru.practicum.personal.storage.PersonalRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.common.util.State.PUBLISHED;

@ExtendWith(MockitoExtension.class)
class PersonalCommentServiceImplTest {

    private PersonalCommentService service;
    private PersonalCommentRepository commentRepository;
    private PersonalEventRepository eventRepository;
    private AdmUserRepository userRepository;
    private PersonalRequestRepository requestRepository;
    private CommentMapper mapper;
    private User eventUser;
    private User commentUser;
    private Event event;
    private Comment commentBeforeEventDate;
    private CommentDtoRequest request;
    private CommentDto expextedCommentDto;

    @BeforeEach
    void setUp() {
        commentRepository = mock(PersonalCommentRepository.class);
        eventRepository = mock(PersonalEventRepository.class);
        userRepository = mock(AdmUserRepository.class);
        requestRepository = mock(PersonalRequestRepository.class);
        mapper = mock(CommentMapper.class);
        service = new PersonalCommentServiceImpl(commentRepository, eventRepository,
                userRepository, requestRepository, mapper);
        eventUser = new User(1L, "mail@ya.ru", "user");
        commentUser = new User(2L, "mail2@ya.ru", "user2");
        event = new Event(1L,
                "annotation",
                new Category(1L, "cat"),
                new ArrayList<>(),
                LocalDateTime.now().minusDays(2),
                "description",
                LocalDateTime.now().plusDays(2),
                eventUser,
                new Location(),
                false,
                0,
                LocalDateTime.now().minusDays(1),
                false,
                PUBLISHED,
                "title",
                0L,
                new ArrayList<>());
        commentBeforeEventDate = new Comment(1L, commentUser, event, "text", LocalDateTime.now().minusDays(1),
                StateComment.PUBLISHED);
        event.setComments(List.of(commentBeforeEventDate));
        request = new CommentDtoRequest();
        ReflectionTestUtils.setField(request, "text", "text");
        expextedCommentDto = new CommentDto(1L, "user2", "text", LocalDateTime.now());
    }

    @Test
    void createComment_whenCommentByUserAlreadyExists_thenThrowException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(commentUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.existsByEvent_idAndRequester_IdAndStatusIs(1L, 2L, StateRequest.CONFIRMED))
                .thenReturn(false);

        assertThrows(EntityNoAccessException.class, () -> service.createComment(2L, 1L, request));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenEventEndedAndCommentatorDontParticipant_thenThrowException() {
        event.setEventDate(LocalDateTime.now().minusHours(6));
        when(userRepository.findById(2L)).thenReturn(Optional.of(commentUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.existsByEvent_idAndRequester_IdAndStatusIs(1L, 2L, StateRequest.CONFIRMED))
                .thenReturn(false);

        assertThrows(EntityNoAccessException.class, () -> service.createComment(2L, 1L, request));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenEventEndedAndCommentatorDidCommentsBeforeAndAfterEvent_thenThrowException() {
        event.setEventDate(LocalDateTime.now().minusHours(6));
        when(userRepository.findById(2L)).thenReturn(Optional.of(commentUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.existsByEvent_idAndRequester_IdAndStatusIs(1L, 2L, StateRequest.CONFIRMED))
                .thenReturn(false);

        assertThrows(EntityNoAccessException.class, () -> service.createComment(2L, 1L, request));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenEventEndedAndCommentatorParticipantDidCommentsBeforeEvent_thenOK() {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        event.setEventDate(time.minusHours(6));
        when(userRepository.findById(2L)).thenReturn(Optional.of(commentUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.existsByEvent_idAndRequester_IdAndStatusIs(1L, 2L, StateRequest.CONFIRMED))
                .thenReturn(true);
        Comment commentBeforeEventDateWithoutId = new Comment(null, commentUser, event, "text",
                LocalDateTime.now().minusDays(1), StateComment.PUBLISHED);
        when(commentRepository.save(commentBeforeEventDateWithoutId)).thenReturn(commentBeforeEventDate);
        when(mapper.convertToComment(request, commentUser, event)).thenReturn(commentBeforeEventDateWithoutId);
        when(mapper.convertToCommentDto(commentBeforeEventDate)).thenReturn(expextedCommentDto);

        CommentDto commentDto = service.createComment(2L, 1L, request);

        assertEquals(expextedCommentDto.getId(), commentDto.getId());
        assertEquals(expextedCommentDto.getText(), commentDto.getText());
        assertEquals(expextedCommentDto.getUserName(), commentDto.getUserName());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void updateComment_whenEventEnded_thenThrowException() {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        event.setEventDate(time.minusHours(6));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.existsByEvent_idAndRequester_IdAndStatusIs(1L, 2L, StateRequest.CONFIRMED))
                .thenReturn(false);

        assertThrows(EntityNoAccessException.class, () -> service.updateComment(2L, 1L, 1L, request));
        verify(commentRepository, never()).save(any());
    }
}