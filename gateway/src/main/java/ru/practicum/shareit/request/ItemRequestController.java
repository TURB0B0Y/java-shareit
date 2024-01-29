package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@SuppressWarnings("unused")
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(
            @RequestBody @Valid CreateItemRequestDto dto,
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        log.info("addRequest {} {}", userId, dto);
        return itemRequestClient.addRequest(dto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        log.info("getRequests {}", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        log.info("getRequests {} {} {}", userId, from, size);
        return itemRequestClient.getRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int requestId
    ) {
        log.info("getRequestById {} {}", userId, requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

}
