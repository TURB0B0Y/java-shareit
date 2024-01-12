package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.APIAccessDeniedException;
import ru.practicum.shareit.exception.APINotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public Item createItem(CreateItemDto dto) {
        User owner = userService.getUserById(dto.getOwnerId());
        Item item = ItemMapper.toModel(dto, owner);
        return itemStorage.save(item);
    }

    @Override
    public Item editItem(ItemDto dto) {
        Item item = getById(dto.getId());
        if (!dto.getOwnerId().equals(item.getOwner().getId())) {
            throw new APIAccessDeniedException("Нет прав на удаление предмета с id %s", dto.getId());
        }
        if (Objects.nonNull(dto.getDescription())) {
            item.setDescription(dto.getDescription());
        }
        if (Objects.nonNull(dto.getName())) {
            item.setName(dto.getName());
        }
        if (Objects.nonNull(dto.getAvailable())) {
            item.setAvailable(dto.getAvailable());
        }
        return itemStorage.save(item);
    }

    @Override
    public void deleteItemById(int itemId, int userId) {
        Item item = getById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new APIAccessDeniedException("Нет прав на удаление предмета с id %s", itemId);
        }
        itemStorage.deleteById(itemId);
    }

    @Override
    public Item getById(int itemId) {
        return itemStorage.findById(itemId)
                .orElseThrow(() -> new APINotFoundException("Предмет %s не найден ", itemId));
    }

    @Override
    public Collection<Item> getAllByOwnerId(int ownerId) {
        return itemStorage.findAllByOwnerId(ownerId);
    }

    @Override
    public List<Item> search(String text) {
        return itemStorage.findAllByNameOrDescription(text);
    }

}
