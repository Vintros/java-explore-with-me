package ru.practicum.common.exceptions;

public class StatsClientResponseException extends RuntimeException {
    public StatsClientResponseException(String message) {
        super(message);
    }
}
