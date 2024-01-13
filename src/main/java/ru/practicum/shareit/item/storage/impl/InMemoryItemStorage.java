package ru.practicum.shareit.item.storage.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
@SuppressWarnings("unused")
public class InMemoryItemStorage implements ItemStorage {

    private final ConcurrentHashMap<Integer, Item> items = new ConcurrentHashMap<>();
    private final AtomicInteger index = new AtomicInteger();

    @Override
    public Item save(Item item) {
        if (Objects.isNull(item.getId())) {
            item.setId(index.incrementAndGet());
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteById(int itemId) {
        items.remove(itemId);
    }

    @Override
    public Optional<Item> findById(int itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> findAllByOwnerId(int ownerId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAllByNameOrDescription(String text) {
        if (Objects.isNull(text) || text.isBlank()) {
            return Collections.emptyList();
        }
        String textInLowerCase = text.toLowerCase(Locale.ROOT);
        return items.values()
                .stream()
                .filter(item -> item.isAvailable()
                        && (item.getName().toLowerCase(Locale.ROOT).contains(textInLowerCase)
                        || item.getDescription().toLowerCase(Locale.ROOT).contains(textInLowerCase)))
                .collect(Collectors.toList());
    }
}
