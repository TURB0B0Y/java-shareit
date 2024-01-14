package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item save(Item item);

    void deleteById(int itemId);

    Optional<Item> findById(int itemId);

    Collection<Item> findAllByOwnerId(int ownerId);

    List<Item> findAllByNameOrDescription(String text);

}
