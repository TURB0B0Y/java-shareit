package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@SuppressWarnings("unused")
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody @Valid CreateItemDto dto, @RequestHeader("X-Sharer-User-Id") int userId) {
        dto.setOwnerId(userId);
        log.info("user {} create item {}", userId, dto);
        Item item = itemService.createItem(dto);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(
            @PathVariable @PositiveOrZero int itemId,
            @RequestBody @Valid ItemDto dto,
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        dto.setOwnerId(userId);
        dto.setId(itemId);
        log.info("user {} edit item {}", userId, dto);
        Item item = itemService.editItem(dto);
        return ItemMapper.toItemDto(item);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItemById(@PathVariable @PositiveOrZero int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("user {} delete item {}", userId, itemId);
        itemService.deleteItemById(itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemBookingCommentDto getItemById(@PathVariable @PositiveOrZero int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("user {} get item {}", userId, itemId);
        return itemService.getItemBookingById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemBookingDto> getAll(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        log.info("user {} get all items {} {}", userId, from, size);
        return itemService.getAllByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        log.info("user {} search item {} {} {}", userId, text, from, size);
        List<Item> items = itemService.search(text, from, size);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(
            @RequestBody @Valid CommentDto dto,
            @PathVariable int itemId,
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        log.info("addComment {} {} {}", userId, itemId, dto);
        Comment comment = itemService.addComment(dto, itemId, userId);
        return CommentMapper.toDto(comment);
    }

}
