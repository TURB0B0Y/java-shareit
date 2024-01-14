package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto.ItemDtoBuilder builder = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable());
        if (Objects.nonNull(item.getRequest())) {
            builder.requestId(item.getRequest().getId());
        }
        if (Objects.nonNull(item.getOwner())) {
            builder.ownerId(item.getOwner().getId());
        }
        return builder.build();
    }

    public static Item toModel(CreateItemDto dto, User owner) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }
}
