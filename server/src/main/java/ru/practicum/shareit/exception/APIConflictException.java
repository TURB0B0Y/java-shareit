package ru.practicum.shareit.exception;

public class APIConflictException extends APIException {

    public APIConflictException(String message, Object... args) {
        super(message, args);
    }

}
