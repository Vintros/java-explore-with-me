package ru.practicum.personal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.CommentDto;
import ru.practicum.common.dto.CommentDtoRequest;
import ru.practicum.personal.service.PersonalCommentService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@RequiredArgsConstructor
@Slf4j
public class PersonalCommentController {

    private final PersonalCommentService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable long userId,
                                    @PathVariable long eventId,
                                    @Valid @RequestBody CommentDtoRequest request) {
        log.info("Create comment to eventId {} by userId: {}", eventId, userId);
        return service.createComment(userId, eventId, request);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentDto updateComment(@PathVariable long userId,
                                    @PathVariable long eventId,
                                    @PathVariable long commentId,
                                    @Valid @RequestBody CommentDtoRequest request) {
        log.info("Update comment with id {} on event {} by userId: {}", commentId, eventId, userId);
        return service.updateComment(userId, eventId, commentId, request);
    }

    @PatchMapping("/{commentId}/check")
    @ResponseStatus(value = HttpStatus.OK)
    public void sendCommentToCheckByEventOwner(@PathVariable long userId,
                                               @PathVariable long eventId,
                                               @PathVariable long commentId) {
        log.info("Send comment with id {} on event {} to check", commentId, eventId);
        service.sendCommentToCheckByEventOwner(userId, eventId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long userId,
                              @PathVariable long eventId,
                              @PathVariable long commentId) {
        log.info("Delete comment with id {} on event {} by userId {}", commentId, eventId, userId);
        service.deleteComment(userId, eventId, commentId);
    }

}
