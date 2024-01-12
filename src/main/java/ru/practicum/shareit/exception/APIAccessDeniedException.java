package ru.practicum.shareit.exception;

public class APIAccessDeniedException extends APIException {

    public APIAccessDeniedException(String message, Object... args) {
        super(message, args);
    }

}
