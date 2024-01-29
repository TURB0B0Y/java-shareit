package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest item) {
        return ItemRequestDto.builder()
                .id(item.getId())
                .created(item.getCreated())
                .description(item.getDescription())
                .requesterId(Objects.isNull(item.getRequester()) ? null : item.getRequester().getId())
                .build();
    }

    public static ItemRequest toModel(CreateItemRequestDto dto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setRequester(user);
        return itemRequest;
    }

    public static ItemRequestWithItemsDto toWithItemsDto(ItemRequest itemRequest) {
        return ItemRequestWithItemsDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems().stream().map(ItemMapper::toItemDto).collect(Collectors.toList()))
                .build();
    }
}
