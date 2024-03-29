package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@SuppressWarnings("unused")
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(
            @RequestBody @Valid CreateItemRequestDto dto,
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        log.info("addRequest {} {}", userId, dto);
        ItemRequest itemRequest = itemRequestService.addRequest(dto, userId);
        return ItemRequestMapper.toDto(itemRequest);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getRequests(
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        log.info("getRequests {}", userId);
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> getRequests(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        log.info("getRequests {} {} {}", userId, from, size);
        return itemRequestService.getRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getRequestById(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int requestId
    ) {
        log.info("getRequestById {} {}", userId, requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }

}
