package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@SuppressWarnings("unused")
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
    public ItemDto getItemById(@PathVariable @PositiveOrZero int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("user {} get item {}", userId, itemId);
        Item item = itemService.getById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("user {} get all items", userId);
        Collection<Item> items = itemService.getAllByOwnerId(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam @NotBlank String text, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("user {} search item {}", userId, text);
        List<Item> items = itemService.search(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
