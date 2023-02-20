package ru.practicum.common.exceptions;

public class RequestNotValidException extends RuntimeException {
    public RequestNotValidException(String message) {
        super(message);
    }
}
