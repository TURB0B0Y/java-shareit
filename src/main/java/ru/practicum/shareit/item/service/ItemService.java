package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    Item createItem(CreateItemDto dto);

    Item editItem(ItemDto dto);

    void deleteItemById(int itemId, int userId);

    Item getById(int itemId);

    Collection<Item> getAllByOwnerId(int ownerId);

    List<Item> search(String text);

}
