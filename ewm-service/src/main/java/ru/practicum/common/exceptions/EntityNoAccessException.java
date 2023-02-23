package ru.practicum.common.exceptions;

public class EntityNoAccessException extends RuntimeException {

    public EntityNoAccessException(String message) {
        super(message);
    }
}
