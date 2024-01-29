package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@SuppressWarnings("unused")
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestBody @Valid CreateItemDto dto, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("user {} create item {}", userId, dto);
        return itemClient.createItem(dto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(
            @PathVariable @PositiveOrZero int itemId,
            @RequestBody @Valid ItemDto dto,
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        log.info("user {} edit item {}", userId, dto);
        return itemClient.editItem(itemId, dto, userId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteItemById(@PathVariable @PositiveOrZero int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("user {} delete item {}", userId, itemId);
        return itemClient.deleteItemById(itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable @PositiveOrZero int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("user {} get item {}", userId, itemId);
        return itemClient.getItemBookingById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        log.info("user {} get all items {} {}", userId, from, size);
        return itemClient.getAllByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        log.info("user {} search item {} {} {}", userId, text, from, size);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestBody @Valid CommentDto dto,
            @PathVariable int itemId,
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        log.info("addComment {} {} {}", userId, itemId, dto);
        return itemClient.addComment(dto, itemId, userId);
    }

}
