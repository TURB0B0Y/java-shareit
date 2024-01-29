package ru.practicum.shareit.exception;

public class APIBadRequestException extends APIException {

    public APIBadRequestException(String message, Object... args) {
        super(message, args);
    }

}
