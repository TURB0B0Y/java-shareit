package ru.practicum.shareit.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.AbstractMap;
import java.util.Map;

@Log4j2
@RestControllerAdvice
@SuppressWarnings("unused")
public class CustomExceptionHandler {

    @ExceptionHandler(APINotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map.Entry<String, String> handleException(APINotFoundException e) {
        return new AbstractMap.SimpleEntry<>("error", e.getMessage());
    }

    @ExceptionHandler(APIConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map.Entry<String, String> handleException(APIConflictException e) {
        return new AbstractMap.SimpleEntry<>("error", e.getMessage());
    }

    @ExceptionHandler(APIAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map.Entry<String, String> handleException(APIAccessDeniedException e) {
        return new AbstractMap.SimpleEntry<>("error", e.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            MissingRequestHeaderException.class,
            NullPointerException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map.Entry<String, String> handleBadRequestException(Exception e) {
        return new AbstractMap.SimpleEntry<>("error", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map.Entry<String, String> handleException(Exception e) {
        log.warn("handled error", e);
        return new AbstractMap.SimpleEntry<>("error", e.getMessage());
    }
}
