package ru.practicum.shareit.exception;

public class APINotFoundException extends APIException {

    public APINotFoundException(String message, Object... args) {
        super(message, args);
    }

}
