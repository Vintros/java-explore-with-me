package ru.practicum.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@RestControllerAdvice("ru.practicum")
public class ErrorHandler {

    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFound(final RuntimeException e) {
        return new ErrorResponse("NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler({EntityNoAccessException.class, RequestNotValidException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse entityNoAccess(final RuntimeException e) {
        return new ErrorResponse("CONFLICT", e.getMessage());
    }

    @ExceptionHandler({IncorrectRequestException.class, ValidationException.class,
            MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(final Exception e) {
        return new ErrorResponse("BAD_REQUEST", e.getMessage());
    }

    @ExceptionHandler({StatsClientResponseException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse statsError(final RuntimeException e) {
        return new ErrorResponse("INTERNAL_SERVER_ERROR", e.getMessage());
    }

}
