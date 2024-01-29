package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    Item createItem(CreateItemDto dto);

    Item editItem(ItemDto dto);

    void deleteItemById(int itemId, int userId);

    Item getById(int itemId);

    ItemBookingCommentDto getItemBookingById(int itemId, int userId);

    Collection<ItemBookingDto> getAllByOwnerId(int ownerId, int from, int size);

    List<Item> search(String text, int from, int size);

    Comment addComment(CommentDto dto, int itemId, int userId);

}
