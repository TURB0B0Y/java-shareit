package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.APIAccessDeniedException;
import ru.practicum.shareit.exception.APIBadRequestException;
import ru.practicum.shareit.exception.APINotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Item createItem(CreateItemDto dto) {
        User owner = getUserById(dto.getOwnerId());
        Item item = ItemMapper.toModel(dto, owner);
        return itemRepository.save(item);
    }

    private User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new APINotFoundException("Пользователь id %s не найден", userId));
    }

    @Override
    @Transactional
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
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteItemById(int itemId, int userId) {
        Item item = getById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new APIAccessDeniedException("Нет прав на удаление предмета с id %s", itemId);
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getById(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new APINotFoundException("Предмет %s не найден ", itemId));
    }

    @Override
    public ItemBookingCommentDto getItemBookingById(int itemId, int userId) {
        Item item = getById(itemId);
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemBookingCommentDto(item, null, null, comments);
        }
        LocalDateTime nowTime = LocalDateTime.now();
        Booking lastBooking = bookingRepository.findFirstByItem_IdAndStartBeforeOrderByEndDesc(itemId, nowTime)
                .orElse(null);
        Booking nextBooking = bookingRepository.findFirstByItem_IdAndStartAfterAndStatusNotOrderByStart(
                itemId,
                nowTime,
                BookingStatus.REJECTED
        ).orElse(null);
        return ItemMapper.toItemBookingCommentDto(item, nextBooking, lastBooking, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemBookingDto> getAllByOwnerId(int ownerId) {

        Collection<Item> items = itemRepository.findAllByOwnerId(ownerId);

        LocalDateTime nowTime = LocalDateTime.now();

        Map<Integer, Booking> lastBookings = bookingRepository.findAllByItemInAndStartBeforeOrderByEndDesc(items, nowTime)
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking, (o, o2) -> o));

        Map<Integer, Booking> nextBookings = bookingRepository.findAllByItemInAndStartAfterAndStatusNotOrderByStart(
                        items,
                        nowTime,
                        BookingStatus.REJECTED
                )
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking, (o, o2) -> o));

        return items
                .stream()
                .map(item -> ItemMapper.toItemBookingDto(
                        item,
                        nextBookings.get(item.getId()),
                        lastBookings.get(item.getId()))
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> search(String text) {
        if (Objects.isNull(text) || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByNameOrDescription(text);
    }

    @Override
    public Comment addComment(CommentDto dto, int itemId, int userId) {
        LocalDateTime nowTime = LocalDateTime.now();
        Booking booking = bookingRepository.findFirstByItem_IdAndBooker_IdAndEndBefore(itemId, userId, nowTime)
                .orElseThrow(() -> new APIBadRequestException("Невозможно оставить комментарий"));
        Comment comment = new Comment();
        comment.setAuthor(booking.getBooker());
        comment.setItem(booking.getItem());
        comment.setText(dto.getText());
        comment.setCreated(nowTime);
        return commentRepository.save(comment);
    }

}
