package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.AbstractMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ExceptionHandlerTest {

    private final CustomExceptionHandler exceptionHandler = new CustomExceptionHandler();

    @Test
    void conflictExcTest() {
        String error = "Test error";
        AbstractMap.SimpleEntry<String, String> expected = new AbstractMap.SimpleEntry<>("error", error);
        Map.Entry<String, String> response = exceptionHandler.handleConflictException(new APIConflictException(error));
        assertEquals(expected, response);
    }

    @Test
    void badReqExcTest() {
        String error = "Test error";
        AbstractMap.SimpleEntry<String, String> expected = new AbstractMap.SimpleEntry<>("error", error);
        Map.Entry<String, String> response = exceptionHandler.handleBadRequestException(new APIBadRequestException(error));
        assertEquals(expected, response);
    }
}