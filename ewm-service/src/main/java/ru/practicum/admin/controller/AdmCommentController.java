package ru.practicum.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.service.AdmCommentService;
import ru.practicum.common.dto.CommentFullDto;
import ru.practicum.common.model.FromSizeRequest;
import ru.practicum.common.util.RequestParamsForComments;
import ru.practicum.common.util.StateComment;
import ru.practicum.common.util.StateCommentAction;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdmCommentController {

    private final AdmCommentService service;

    @GetMapping("/comments")
    public List<CommentFullDto> getComments(
            @RequestParam(required = false) long user,
            @RequestParam(required = false) StateComment state,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = FromSizeRequest.of(from, size, sort);
        RequestParamsForComments params = new RequestParamsForComments(user, state, rangeStart, rangeEnd);
        log.info("Get comments by params: {}", params);
        return service.getComments(params, pageable);
    }

    @PatchMapping("/comments")
    public List<CommentFullDto> inspectComments(@RequestParam List<Long> commentsId,
                                                @RequestParam StateCommentAction action) {
        log.info("Considerate comments with ids {} to state action {}", commentsId, action);
        return service.inspectComments(commentsId, action);
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long eventId,
                              @PathVariable long commentId) {
        log.info("Delete comment with id {} on event {}", commentId, eventId);
        service.deleteComment(eventId, commentId);
    }

}
